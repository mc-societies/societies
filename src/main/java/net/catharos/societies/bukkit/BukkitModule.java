package net.catharos.societies.bukkit;

import net.catharos.lib.shank.AbstractModule;
import net.catharos.societies.WorldProvider;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Represents a BukkitModule
 */
public class BukkitModule extends AbstractModule {

    private final Server server;
    private final Plugin plugin;

    public BukkitModule(Server server, Plugin plugin) {
        this.server = server;
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(Server.class).toInstance(server);
        bind(Plugin.class).toInstance(plugin);
        bind(BukkitScheduler.class).toInstance(server.getScheduler());
        bind(ConsoleCommandSender.class).toInstance(server.getConsoleSender());
        bindNamed("default-world", World.class).toInstance(server.getWorlds().get(0));

        bind(WorldProvider.class).to(BukkitWorldProvider.class);
    }
}
