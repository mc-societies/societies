package net.catharos.societies.bukkit;

import net.catharos.lib.shank.AbstractModule;
import net.catharos.societies.WorldProvider;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;

/**
 * Represents a BukkitModule
 */
public class BukkitModule extends AbstractModule {

    private final Server server;

    public BukkitModule(Server server) {
        this.server = server;
    }

    @Override
    protected void configure() {
        bind(Server.class).toInstance(server);
        bind(ConsoleCommandSender.class).toInstance(server.getConsoleSender());
        bindNamed("default-world", World.class).toInstance(server.getWorlds().get(0));

        bind(WorldProvider.class).to(BukkitWorldProvider.class);
    }
}
