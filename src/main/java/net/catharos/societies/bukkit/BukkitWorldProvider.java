package net.catharos.societies.bukkit;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.societies.WorldProvider;
import org.bukkit.Server;
import org.bukkit.World;

/**
 * Represents a BukkitWorldProvider
 */
public class BukkitWorldProvider implements WorldProvider {

    private final Server server;
    private final World defaultWorld;

    @Inject
    public BukkitWorldProvider(Server server, @Named("default-world") World defaultWorld) {
        this.server = server;
        this.defaultWorld = defaultWorld;
    }

    @Override
    public World getWorld(String name) {
        return server.getWorld(name);
    }

    @Override
    public World getDefaultWorld() {
        return defaultWorld;
    }
}
