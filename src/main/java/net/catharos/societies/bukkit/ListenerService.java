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
    private final DamageListener damageListener;

    @InjectLogger
    private Logger logger;

    @Inject
    public ListenerService(Server server, Plugin plugin, DamageListener damageListener) {
        this.server = server;
        this.plugin = plugin;
        this.damageListener = damageListener;
    }

    @Override
    public void start(LifecycleContext context) throws Exception {
        logger.info("Register event listeners...");
        server.getPluginManager().registerEvents(damageListener, plugin);
    }
}
