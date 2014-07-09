package net.catharos.societies.member;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import net.catharos.groups.DefaultMember;
import net.catharos.groups.request.Request;
import net.catharos.societies.PlayerProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Provider;
import java.util.UUID;

/**
 * Represents a SocietyMember
 */
public class BukkitSocietyMember extends DefaultMember implements SocietyMember {

    private Request activeRequest;
    private final PlayerProvider<Player> playerProvider;

    @Inject
    public BukkitSocietyMember(Provider<UUID> uuid, PlayerProvider<Player> playerProvider) {
        this(uuid.get(), playerProvider);
    }

    @AssistedInject
    public BukkitSocietyMember(@Assisted UUID uuid, PlayerProvider<Player> playerProvider) {
        super(uuid);
        this.playerProvider = playerProvider;
    }

    @Override
    public void send(String message) {
        Player player = toPlayer();
        if (player == null) {
            return;
        }

        player.sendMessage(message);
        System.out.println(message);
    }

    @Override
    public String getName() {
        Player player = toPlayer();

        if (player != null) {
            return player.getName();
        }

        throw new RuntimeException("Player is not online!");
    }

    @Override
    public void send(String message, Object... args) {
        send(String.format(message, args));
    }

    @Override
    public void send(StringBuilder message) {
        send(message.toString());
    }

    @Override
    @Nullable
    public Player toPlayer() {
        return playerProvider.getPlayer(getUUID());
    }
}
