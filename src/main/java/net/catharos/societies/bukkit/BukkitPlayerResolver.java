package net.catharos.societies.bukkit;

import com.google.inject.Inject;
import net.catharos.societies.PlayerResolver;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents a BukkitPlayerProvider
 */
public class BukkitPlayerResolver implements PlayerResolver {

    private final Server sender;

    @Inject
    public BukkitPlayerResolver(Server sender) {this.sender = sender;}

    @Nullable
    @Override
    public UUID getPlayer(String name) {
        Player player = Bukkit.getPlayer(name);

        if (player == null) {
            return null;
        }

        return player.getUniqueId();
    }

    @Override
    public boolean isAvailable(String name) {
        return getPlayer(name) != null;
    }

    @Override
    public boolean isAvailable(UUID uuid) {
        try {
            return sender.getPlayer(uuid) != null;
        } catch (NullPointerException e) {
            throw new RuntimeException("Bukkit is not active!", e);
        }
    }
}
