package net.catharos.societies.bukkit;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.societies.NameProvider;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

import java.util.UUID;

/**
 * Represents a BukkitNameProvider
 */
public class BukkitNameProvider implements NameProvider {

    private final NameProvider fallback;
    private final Server server;

    @Inject
    public BukkitNameProvider(@Named("fallback") NameProvider fallback, Server server) {
        this.fallback = fallback;
        this.server = server;
    }

    @Override
    public String getName(UUID uuid) {
        OfflinePlayer offlinePlayer = server.getOfflinePlayer(uuid);

        if (offlinePlayer == null) {
            String name = fallback.getName(uuid);

            if (name != null) {
                return name;
            }

            return uuid.toString();
        }

        return offlinePlayer.getName();
    }
}
