package net.catharos.societies.bukkit;

import com.google.inject.Inject;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.lib.shank.service.AbstractService;
import net.catharos.lib.shank.service.lifecycle.LifecycleContext;
import org.apache.logging.log4j.Logger;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * Represents a ListenerService
 */
public class ListenerService extends AbstractService {

    private final Server server;
    private final Plugin plugin;

    @InjectLogger
    private Logger logger;

    @Inject
    public ListenerService(Server server, Plugin plugin) {
        this.server = server;
        this.plugin = plugin;
    }

    @Override
    public void start(LifecycleContext context) throws Exception {
        logger.info("Register event listeners...");
        PluginManager pluginManager = server.getPluginManager();
        pluginManager.registerEvents(context.get(ChatListener.class), plugin);
        pluginManager.registerEvents(context.get(DamageListener.class), plugin);
    }
}
