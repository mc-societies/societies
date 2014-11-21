package net.catharos.societies.member;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import gnu.trove.map.hash.THashMap;
import net.catharos.groups.Group;
import net.catharos.groups.GroupProvider;
import net.catharos.groups.Member;
import net.catharos.groups.MemberCache;
import net.catharos.groups.rank.Rank;
import net.catharos.societies.api.PlayerResolver;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Represents a OnlineCacheMemberProvider
 */
@Singleton
public class OnlineMemberCache<M extends Member> implements MemberCache<M> {

    private final THashMap<UUID, M> members = new THashMap<UUID, M>();

    private final GroupProvider groupProvider;
    private final PlayerResolver provider;

    @Inject
    public OnlineMemberCache(GroupProvider groupProvider, PlayerResolver provider) {
        this.groupProvider = groupProvider;
        this.provider = provider;
    }

    @Override
    public M getMember(UUID uuid) {
        M member = members.get(uuid);

        if (member == null) {
            return null;
        }

        //fixme group is maybe out of date: Currently, update group and ranks!
        Group group = member.getGroup();

        if (group != null) {
            member.complete(false);
            try {
                member.setGroup(groupProvider.getGroup(group.getUUID()).get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            for (Rank rank : member.getRanks()) {
                if (group.getRank(rank.getUUID()) == null) {
                    member.removeRank(rank);
                }
            }

            member.complete();
        }
        return member;
    }

    @Override
    public M getMember(String name) {
        UUID player = provider.getPlayer(name);
        return members.get(player);
    }

    @Override
    public Set<M> getMembers() {
        return null;
    }

    public M clear(UUID uuid) {
        return this.members.remove(uuid);
    }

    @Override
    public boolean cache(M member) {
        return members.put(member.getUUID(), member) == null;
    }

    @Override
    public M clear(M member) {
        return this.clear(member.getUUID());
    }
}
