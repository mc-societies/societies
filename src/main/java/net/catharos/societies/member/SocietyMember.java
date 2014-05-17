package net.catharos.societies.member;

import net.catharos.groups.DefaultMember;
import net.catharos.lib.core.command.sender.Sender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents a SocietyMember
 */
public class SocietyMember extends DefaultMember implements Sender {
    private final PlayerProvider<Player> playerProvider;

    public SocietyMember(UUID uuid, PlayerProvider<Player> playerProvider) {
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
    }

    @Nullable
    public Player toPlayer() {
        return playerProvider.getPlayer(getUUID());
    }
}
