package net.catharos.societies.bukkit;

import com.google.inject.Inject;
import net.catharos.societies.NameProvider;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

import java.util.UUID;

/**
 * Represents a BukkitNameProvider
 */
public class BukkitNameProvider implements NameProvider {

    private final Server server;

    @Inject
    public BukkitNameProvider(Server server) {this.server = server;}

    @Override
    public String getName(UUID uuid) {
        OfflinePlayer offlinePlayer = server.getOfflinePlayer(uuid);

        if (offlinePlayer == null) {
            return uuid.toString();
        }
        return offlinePlayer.getName();
    }
}
