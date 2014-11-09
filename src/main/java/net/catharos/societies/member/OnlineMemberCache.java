package net.catharos.societies.member;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import gnu.trove.map.hash.THashMap;
import net.catharos.groups.Member;
import net.catharos.groups.MemberCache;
import net.catharos.societies.PlayerResolver;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a OnlineCacheMemberProvider
 */
@Singleton
public class OnlineMemberCache<M extends Member> implements MemberCache<M> {

    private final THashMap<UUID, M> members = new THashMap<UUID, M>();

    private final PlayerResolver provider;

    @Inject
    public OnlineMemberCache(PlayerResolver provider) {
        this.provider = provider;
    }

    @Override
    public M getMember(UUID uuid) {
        return members.get(uuid);
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
