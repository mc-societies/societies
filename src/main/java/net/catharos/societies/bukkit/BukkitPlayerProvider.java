package net.catharos.societies.bukkit;

import com.google.inject.Inject;
import net.catharos.societies.PlayerProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Represents a BukkitPlayerProvider
 */
public class BukkitPlayerProvider implements PlayerProvider {

    private final Server sender;

    @Inject
    public BukkitPlayerProvider(Server sender) {this.sender = sender;}

    @Override
    public Player getPlayer(String name) {
        return Bukkit.getPlayer(name);
    }

    @Override
    public Player getPlayer(UUID uuid) {
        try {
            return sender.getPlayer(uuid);
        } catch (NullPointerException e) {
            throw new RuntimeException("Bukkit is not active!", e);
        }
    }

    @Override
    public OfflinePlayer getOfflinePlayer(UUID uuid) {
        try {
            return sender.getPlayer(uuid);
        } catch (NullPointerException e) {
            throw new RuntimeException("Bukkit is not active!", e);
        }
    }
}
