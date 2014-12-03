package org.societies.database.json;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.googlecode.cqengine.CQEngine;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.index.Index;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.index.suffix.SuffixTreeIndex;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.resultset.ResultSet;
import gnu.trove.map.hash.THashMap;
import net.catharos.lib.core.util.CastSafe;
import net.catharos.lib.core.uuid.UUIDStorage;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.lib.shank.service.AbstractService;
import net.catharos.lib.shank.service.lifecycle.LifecycleContext;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.societies.api.PlayerResolver;
import org.societies.bridge.ChatColor;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupProvider;
import org.societies.groups.member.*;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

import static com.google.common.util.concurrent.Futures.immediateFuture;
import static com.googlecode.cqengine.query.QueryFactory.equal;

/**
 * Represents a JSONProvider
 */
@Singleton
public class JSONProvider extends AbstractService implements MemberProvider, GroupProvider, MemberPublisher, MemberDestructor {

    IndexedCollection<Group> groups = CQEngine.newInstance();

    public static final Attribute<Group, UUID> GROUP_UUID = new SimpleAttribute<Group, UUID>("group_uuid") {
        @Override
        public UUID getValue(Group group) { return group.getUUID(); }
    };

    public static final Attribute<Group, String> GROUP_TAG = new SimpleAttribute<Group, String>("group_tag") {
        @Override
        public String getValue(Group group) {
            return group.getTag();
        }
    };

    public static final Attribute<Group, String> GROUP_CLEAN_TAG = new SimpleAttribute<Group, String>("group_tag") {
        @Override
        public String getValue(Group group) { return ChatColor.stripColor(group.getTag()); }
    };


    {
        groups.addIndex(HashIndex.onAttribute(GROUP_UUID));

        groups.addIndex(HashIndex.onAttribute(GROUP_TAG));
        groups.addIndex(SuffixTreeIndex.onAttribute(GROUP_TAG));

        groups.addIndex(HashIndex.onAttribute(GROUP_CLEAN_TAG));
    }

    IndexedCollection<Member> members = CQEngine.newInstance();

    public final Attribute<Member, UUID> MEMBER_UUID = new SimpleAttribute<Member, UUID>("group_uuid") {
        @Override
        public UUID getValue(Member member) { return member.getUUID(); }
    };


    {
        members.addIndex(CastSafe.<Index<Member>>toGeneric(HashIndex.onAttribute(MEMBER_UUID)));
    }

    private final PlayerResolver playerResolver;
    private final GroupMapper groupMapper;
    private final MemberMapper mapper;
    private final UUIDStorage memberStorage;
    private final UUIDStorage groupStorage;
    private final ListeningExecutorService service;

    private MemberPublisher memberPublisher;

    private final MemberFactory memberFactory;

    @InjectLogger
    private Logger logger;

    @Inject
    public JSONProvider(PlayerResolver playerResolver,
                        MemberMapper mapper,
                        @Named("group-root") File groupRoot,
                        @Named("member-root") File memberRoot,
                        GroupMapper groupMapper,
                        ListeningExecutorService service, MemberFactory memberFactory) {
        this.playerResolver = playerResolver;
        this.mapper = mapper;
        this.groupMapper = groupMapper;
        this.service = service;
        this.memberFactory = memberFactory;
        this.groupStorage = new UUIDStorage(groupRoot, "json");
        this.memberStorage = new UUIDStorage(memberRoot, "json");
    }

    @Inject
    public void publisher(MemberPublisher memberPublisher) {  //beautify
        this.memberPublisher = memberPublisher;
    }

    @Override
    public void init(LifecycleContext context) throws Exception {

        final THashMap<UUID, Group> temp = new THashMap<UUID, Group>();

        for (File file : groupStorage) {
            if (!file.exists()) {
                continue;
            }
            try {
                Group group = groupMapper.readGroup(file);

                temp.put(group.getUUID(), group);
                groups.add(group);
            } catch (Throwable e) {
                logger.error("Failed loading group from file " + file + "!", e);
            }
        }

        for (File file : memberStorage) {
            if (!file.exists()) {
                continue;
            }
            try {
                members.add(mapper.readMember(file, new Function<UUID, Group>() {
                    @Nullable
                    @Override
                    public Group apply(UUID input) {
                        return temp.get(input);
                    }
                }));
            } catch (Throwable e) {
                logger.error("Failed loading member from file " + file + "!", e);
            }
        }

        logger.info("Loaded societies and members! Ready to rock on!");
    }

    @Override
    public ListenableFuture<Group> getGroup(UUID uuid) {
        Query<Group> query = equal(GROUP_UUID, uuid);
        ResultSet<Group> retrieve = groups.retrieve(query);

        return immediateFuture(Iterables.getOnlyElement(retrieve, null));
    }

    @Override
    public ListenableFuture<Set<Group>> getGroup(String tag) {
        Query<Group> query = equal(GROUP_TAG, tag);

        ResultSet<Group> retrieve = groups.retrieve(query);

        Set<Group> result = Sets.newHashSet(retrieve);
        return immediateFuture(result);
    }

    @Override
    public ListenableFuture<Set<Group>> getGroups() {
        Set<Group> result = Sets.newHashSet(groups);
        return immediateFuture(result);
    }

    @Override
    public ListenableFuture<Integer> size() {
        return immediateFuture(groups.size());
    }

    @Override
    public ListenableFuture<Member> getMember(UUID uuid) {
        Query<Member> query = equal(MEMBER_UUID, uuid);
        ResultSet<Member> retrieve = members.retrieve(query);

        if (retrieve.isEmpty()) {
            Member member = memberFactory.create(uuid);
            memberPublisher.publish(member);
            members.add(member);
            return immediateFuture(member);
        }

        return immediateFuture(Iterables.getOnlyElement(retrieve, null));
    }

    @Override
    public ListenableFuture<Member> getMember(String name) {
        UUID player = playerResolver.getPlayer(name);

        if (player == null) {
            return immediateFuture(null);
        }

        return getMember(player);
    }

    @Override
    public ListenableFuture<Set<Member>> getMembers() {
        Set<Member> result = Collections.unmodifiableSet(members);
        return immediateFuture(result);
    }

    @Override
    public ListenableFuture<Member> publish(final Member member) {
        return service.submit(new Callable<Member>() {

            @Override
            public Member call() throws Exception {
                try {
                    members.add(member);//beautify cast?
                    mapper.writeMember(member, memberStorage.getFile(member.getUUID()));
                } catch (Exception e) {
                    logger.catching(e);
                }

                return member;
            }
        });
    }

    @Override
    public ListenableFuture destruct(final Member member) {
        return service.submit(new Callable<Member>() {

            @Override
            public Member call() throws Exception {
                members.remove(member);

                File file = memberStorage.getFile(member.getUUID());
                FileUtils.forceDelete(file);
                return member;
            }
        });
    }
}
