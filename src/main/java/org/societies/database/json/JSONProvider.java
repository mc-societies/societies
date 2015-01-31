package org.societies.database.json;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
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
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.shank.logging.InjectLogger;
import org.shank.service.AbstractService;
import org.shank.service.lifecycle.LifecycleContext;
import org.societies.api.PlayerResolver;
import org.societies.bridge.ChatColor;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupProvider;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberFactory;
import org.societies.groups.member.MemberProvider;
import org.societies.groups.member.MemberPublisher;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static com.googlecode.cqengine.query.QueryFactory.equal;

/**
 * Represents a JSONProvider
 */
@Singleton
class JSONProvider extends AbstractService implements MemberProvider, GroupProvider, MemberPublisher {

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
    public Group getGroup(UUID uuid) {
        Query<Group> query = equal(GROUP_UUID, uuid);
        ResultSet<Group> retrieve = groups.retrieve(query);

        return Iterables.getOnlyElement(retrieve, null);
    }

    @Override
    public Set<Group> getGroup(String tag) {
        Query<Group> query = equal(GROUP_TAG, tag);

        ResultSet<Group> retrieve = groups.retrieve(query);

        return Sets.newHashSet(retrieve);
    }

    @Override
    public Set<Group> getGroups() {
        return Sets.newHashSet(groups);
    }

    @Override
    public Integer size() {
        return groups.size();
    }

    @Override
    public Member getMember(UUID uuid) {
        Query<Member> query = equal(MEMBER_UUID, uuid);
        ResultSet<Member> retrieve = members.retrieve(query);

        if (retrieve.isEmpty()) {
            Member member = memberFactory.create(uuid);
            memberPublisher.publish(member);
            members.add(member);
            return member;
        }

        return Iterables.getOnlyElement(retrieve, null);
    }

    @Override
    public Member getMember(String name) {
        UUID player = playerResolver.getPlayer(name);

        if (player == null) {
            return null;
        }

        return getMember(player);
    }

    @Override
    public Set<Member> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    @Override
    public Member publish(final Member member) {

        try {
            members.add(member);//beautify cast?
            mapper.writeMember(member, memberStorage.getFile(member.getUUID()));
        } catch (Exception e) {
            logger.catching(e);
        }

        return member;
    }

    @Override
    public Member destruct(final Member member) {
        members.remove(member);

        try {
            File file = memberStorage.getFile(member.getUUID());
            FileUtils.forceDelete(file);
        } catch (IOException e) {
            e.printStackTrace();      //fixme
            return null;
        }

        return member;
    }
}
