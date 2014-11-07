package net.catharos.societies.bukkit;

import com.google.inject.Inject;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.lib.shank.service.AbstractService;
import net.catharos.lib.shank.service.lifecycle.LifecycleContext;
import org.apache.logging.log4j.Logger;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

/**
 * Represents a ListenerService
 */
public class ListenerService extends AbstractService {

    private final Server server;
    private final Plugin plugin;
    private final EntityListener entityListener;

    @InjectLogger
    private Logger logger;

    @Inject
    public ListenerService(Server server, Plugin plugin, EntityListener entityListener) {
        this.server = server;
        this.plugin = plugin;
        this.entityListener = entityListener;
    }

    @Override
    public void start(LifecycleContext context) throws Exception {
        logger.info("Register event listeners...");
        server.getPluginManager().registerEvents(entityListener, plugin);
    }
}
