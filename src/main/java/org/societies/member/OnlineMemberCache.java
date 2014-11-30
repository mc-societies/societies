package org.societies.member;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import gnu.trove.map.hash.THashMap;
import org.societies.api.PlayerResolver;
import org.societies.groups.cache.MemberCache;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupProvider;
import org.societies.groups.member.Member;
import org.societies.groups.rank.Rank;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Represents a OnlineCacheMemberProvider
 */
@Singleton
public class OnlineMemberCache implements MemberCache {

    private final THashMap<UUID, Member> members = new THashMap<UUID, Member>();

    private final GroupProvider groupProvider;
    private final PlayerResolver provider;

    @Inject
    public OnlineMemberCache(GroupProvider groupProvider, PlayerResolver provider) {
        this.groupProvider = groupProvider;
        this.provider = provider;
    }

    @Override
    public Member getMember(UUID uuid) {
        Member member = members.get(uuid);

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
    public Member getMember(String name) {
        UUID player = provider.getPlayer(name);
        return members.get(player);
    }

    @Override
    public Set<Member> getMembers() {
        return null;
    }

    public Member clear(UUID uuid) {
        return this.members.remove(uuid);
    }

    @Override
    public boolean cache(Member member) {
        return members.put(member.getUUID(), member) == null;
    }

    @Override
    public Member clear(Member member) {
        return this.clear(member.getUUID());
    }
}
