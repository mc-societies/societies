package net.catharos.societies.member;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import gnu.trove.map.hash.THashMap;
import net.catharos.groups.Member;
import net.catharos.groups.MemberCache;
import net.catharos.societies.PlayerProvider;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Represents a OnlineCacheMemberProvider
 */
@Singleton
public class OnlineMemberCache<M extends Member> implements MemberCache<M> {

    private final THashMap<UUID, M> members = new THashMap<UUID, M>();

    private final PlayerProvider provider;

    @Inject
    public OnlineMemberCache(PlayerProvider provider) {
        this.provider = provider;
    }

    @Override
    public M getMember(UUID uuid) {
        return members.get(uuid);
    }

    @Override
    public M getMember(String name) {
        Player player = provider.getPlayer(name);
        return members.get(player.getUniqueId());
    }

    public M clear(UUID uuid) {
        return this.members.remove(uuid);
    }

    @Override
    public void cache(M member) {
        members.put(member.getUUID(), member);
    }
}
