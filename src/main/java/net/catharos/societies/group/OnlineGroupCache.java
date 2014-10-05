package net.catharos.societies.group;

import com.google.inject.Singleton;
import gnu.trove.map.hash.THashMap;
import net.catharos.groups.Group;
import net.catharos.groups.GroupCache;
import net.catharos.groups.Member;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a OnlineCacheMemberProvider
 */
@Singleton
public class OnlineGroupCache implements GroupCache {

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

    public Group clear(UUID uuid) {
        return this.groups.remove(uuid);
    }

    public void clear(Member leaving, Group group) {
        if (group == null) {
            return;
        }

        Set<Member> members = group.getMembers();

        for (Member member : members) {
            if (member.equals(leaving)) {
                continue;
            }

            if (member.isAvailable()) {
                return;
            }
        }

        clear(group);
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
