package net.catharos.societies;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Represents a PlayerProvider
 */
public interface PlayerProvider {

    Player getPlayer(String name);

    Player getPlayer(UUID uuid);

    OfflinePlayer getOfflinePlayer(UUID uuid);

}
