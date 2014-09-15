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
        //todo cache by name
        return null;
    }

    @Override
    public Set<Group> getGroups() {
        return null;
    }

    public void clear(UUID uuid) {
        this.groups.remove(uuid);
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
                clear(group.getUUID());
            }
        }
    }

    @Override
    public void cache(Group group) {
        groups.put(group.getUUID(), group);
    }

    @Override
    public void clear(Group group) {
        clear(group.getUUID());
    }
}
