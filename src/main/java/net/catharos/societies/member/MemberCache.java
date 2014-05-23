package net.catharos.societies.member;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.catharos.groups.MemberProvider;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.command.sender.SenderProvider;
import net.catharos.societies.PlayerProvider;
import net.catharos.societies.cache.Cache;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Represents a MemberCache
 */
@Singleton
public class MemberCache extends Cache<SocietyMember> implements MemberProvider<SocietyMember>, SenderProvider {

    public static final int MAX_CACHED = 250;

    public static final int MEMBER_LIFE_TIME = 2;


    private final PlayerProvider<Player> playerProvider;
    private final MemberProvider<SocietyMember> sourceProvider;

    @Inject
    public MemberCache(PlayerProvider<Player> playerProvider, @Named("source-member-provider") MemberProvider<SocietyMember> sourceProvider) {
        super(MAX_CACHED, MEMBER_LIFE_TIME, TimeUnit.HOURS);
        this.playerProvider = playerProvider;
        this.sourceProvider = sourceProvider;
    }

    @Override
    public SocietyMember getMember(UUID uuid) {
        return get(uuid);
    }

    @Override
    public SocietyMember getMember(String name) {
        Player player = playerProvider.getPlayer(name);

        if (player == null) {
            return null;
        }

        return getMember(player.getUniqueId());
    }

    public Iterable<SocietyMember> getMembers() {
        return asMap().values();
    }

    @Override
    public SocietyMember load(@NotNull UUID uuid) throws Exception {
        return sourceProvider.getMember(uuid);
    }

    @Nullable
    @Override
    public Sender getSender(String name) {
        return getMember(name);
    }

    @Nullable
    @Override
    public Sender getSender(UUID uuid) {
        return getMember(uuid);
    }
}
