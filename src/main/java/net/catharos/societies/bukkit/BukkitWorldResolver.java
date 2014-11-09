package net.catharos.societies.bukkit;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.societies.bridge.WorldResolver;
import net.catharos.societies.bridge.World;
import net.catharos.societies.bukkit.bridge.BukkitWorld;
import org.bukkit.Server;

/**
 * Represents a BukkitWorldProvider
 */
public class BukkitWorldResolver implements WorldResolver {

    private final Server server;
    private final World defaultWorld;

    @Inject
    public BukkitWorldResolver(Server server, @Named("default-world") World defaultWorld) {
        this.server = server;
        this.defaultWorld = defaultWorld;
    }

    @Override
    public World getWorld(String name) {
        return new BukkitWorld(server.getWorld(name));
    }

    @Override
    public World getDefaultWorld() {
        return defaultWorld;
    }
}
