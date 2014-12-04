package org.societies.member;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import gnu.trove.map.hash.THashMap;
import org.societies.api.PlayerResolver;
import org.societies.groups.cache.MemberCache;
import org.societies.groups.member.Member;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a OnlineCacheMemberProvider
 */
@Singleton
public class OnlineMemberCache implements MemberCache {

    private final THashMap<UUID, Member> members = new THashMap<UUID, Member>();

    private final PlayerResolver provider;

    @Inject
    public OnlineMemberCache(PlayerResolver provider) {
        this.provider = provider;
    }

    @Override
    public Member getMember(UUID uuid) {
        Member member = members.get(uuid);

        if (member == null) {
            return null;
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
