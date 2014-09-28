package net.catharos.societies.database.json;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlecode.cqengine.CQEngine;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.index.suffix.SuffixTreeIndex;
import com.googlecode.cqengine.index.unique.UniqueIndex;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.resultset.ResultSet;
import net.catharos.groups.Group;
import net.catharos.groups.GroupProvider;
import net.catharos.groups.Member;
import net.catharos.groups.MemberProvider;
import net.catharos.lib.shank.service.AbstractService;
import net.catharos.lib.shank.service.lifecycle.LifecycleContext;
import net.catharos.societies.PlayerProvider;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

import static com.google.common.util.concurrent.Futures.immediateFuture;
import static com.googlecode.cqengine.query.QueryFactory.contains;
import static com.googlecode.cqengine.query.QueryFactory.equal;

/**
 * Represents a JSONProvider
 */
@Singleton
public class JSONProvider<M extends Member> extends AbstractService implements MemberProvider<M>, GroupProvider {

    IndexedCollection<Group> groups = CQEngine.newInstance();

    public static final Attribute<Group, UUID> GROUP_UUID = new SimpleAttribute<Group, UUID>("group_uuid") {
        @Override
        public UUID getValue(Group group) { return group.getUUID(); }
    };

    public static final Attribute<Group, String> GROUP_NAME = new SimpleAttribute<Group, String>("group_name") {
        @Override
        public String getValue(Group group) { return group.getName(); }
    };

    {
        groups.addIndex(UniqueIndex.onAttribute(GROUP_UUID));

        groups.addIndex(HashIndex.onAttribute(GROUP_UUID));
        groups.addIndex(SuffixTreeIndex.onAttribute(GROUP_NAME));
    }

    IndexedCollection<M> members = CQEngine.newInstance();

    public final Attribute<M, UUID> MEMBER_UUID = new SimpleAttribute<M, UUID>("group_uuid") {
        @Override
        public UUID getValue(M member) { return member.getUUID(); }
    };


    {
        members.addIndex(UniqueIndex.onAttribute(MEMBER_UUID));

        members.addIndex(HashIndex.onAttribute(MEMBER_UUID));
    }

    private final PlayerProvider playerProvider;
    private final SocietyMapper societyMapper;

    @Inject
    public JSONProvider(PlayerProvider playerProvider, SocietyMapper societyMapper) {
        this.playerProvider = playerProvider;
        this.societyMapper = societyMapper;
    }

    @Override
    public void init(LifecycleContext context) throws Exception {

    }

    @Override
    public ListenableFuture<Group> getGroup(UUID uuid) {
        Query<Group> query = equal(GROUP_UUID, uuid);
        ResultSet<Group> retrieve = groups.retrieve(query);

        return Futures.immediateFuture(Iterables.getOnlyElement(retrieve));
    }

    @Override
    public ListenableFuture<Set<Group>> getGroup(String name) {
        Query<Group> query = contains(GROUP_NAME, name);
        ResultSet<Group> retrieve = groups.retrieve(query);

        Set<Group> result = Sets.newHashSet(retrieve);
        return Futures.immediateFuture(result);
    }

    @Override
    public ListenableFuture<Set<Group>> getGroups() {
        Set<Group> result = Sets.newHashSet(groups);
        return Futures.immediateFuture(result);
    }

    @Override
    public ListenableFuture<M> getMember(UUID uuid) {
        Query<M> query = equal(MEMBER_UUID, uuid);
        ResultSet<M> retrieve = members.retrieve(query);

        return Futures.immediateFuture(retrieve.uniqueResult());
    }

    @Override
    public ListenableFuture<M> getMember(String name) {
        Player player = playerProvider.getPlayer(name);

        if (player == null) {
            return immediateFuture(null);
        }

        return getMember(player.getUniqueId());
    }
}
