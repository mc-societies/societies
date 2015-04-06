package org.societies.bukkit;

import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.jetbrains.annotations.Nullable;
import org.societies.api.PlayerResolver;

import java.util.UUID;

/**
 * Represents a BukkitPlayerProvider
 */
class BukkitPlayerResolver implements PlayerResolver {

    private final Server sender;

    @Inject
    public BukkitPlayerResolver(Server sender) {
        this.sender = sender;
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public UUID getPlayer(String name) {
        OfflinePlayer player = Bukkit.getPlayer(name);

        if (player == null) {

            //optimize
            OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();

            for (OfflinePlayer offlinePlayer : offlinePlayers) {
                if (offlinePlayer.getName().startsWith(name)) {
                    return offlinePlayer.getUniqueId();
                }
            }

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
