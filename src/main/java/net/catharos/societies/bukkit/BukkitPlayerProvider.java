package net.catharos.societies.bukkit;

import net.catharos.societies.PlayerProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Represents a BukkitPlayerProvider
 */
public class BukkitPlayerProvider implements PlayerProvider {

    @Override
    public Player getPlayer(String name) {
        return Bukkit.getPlayer(name);
    }

    @Override
    public Player getPlayer(UUID uuid) {
        try {
            return Bukkit.getPlayer(uuid);
        } catch (NullPointerException e) {
            throw new RuntimeException("Bukkit is not active!", e);
        }
    }

    @Override
    public OfflinePlayer getOfflinePlayer(UUID uuid) {
        try {
            return Bukkit.getPlayer(uuid);
        } catch (NullPointerException e) {
            throw new RuntimeException("Bukkit is not active!", e);
        }
    }
}
