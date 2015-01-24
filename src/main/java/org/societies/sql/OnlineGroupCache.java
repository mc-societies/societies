package org.societies.sql;

import com.google.inject.Singleton;
import gnu.trove.map.hash.THashMap;
import org.societies.bridge.Player;
import org.societies.groups.cache.GroupCache;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a OnlineCacheMemberProvider
 */
@Singleton
class OnlineGroupCache implements GroupCache {

    private final THashMap<UUID, Group> groups = new THashMap<UUID, Group>();

    @Override
    public Group getGroup(UUID uuid) {
        return tryGet(uuid);
    }

    private Group tryGet(UUID key) {
        final Group group = groups.get(key);

        if (group != null) {
            return group;
        }

        return null;
    }

    @Override
    public Set<Group> getGroup(String name) {
        return null;
    }

    @Override
    public Set<Group> getGroups() {
        return null;
    }

    @Override
    public int size() {
        return -1;
    }

    public Group clear(UUID uuid) {
        return this.groups.remove(uuid);
    }

    @Override
    public Group clear(Member leaving, Group group) {
        if (group == null) {
            return null;
        }

        Set<Member> members = group.getMembers();

        for (Member member : members) {
            if (member.equals(leaving)) {
                continue;
            }

            if (member.get(Player.class).isAvailable()) {
                return null;
            }
        }

        clear(group);
        return group;
    }

    @Override
    public boolean cache(Group group) {
        return groups.put(group.getUUID(), group) == null;
    }

    @Override
    public Group clear(Group group) {
        return clear(group.getUUID());
    }
}
