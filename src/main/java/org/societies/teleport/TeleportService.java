package org.societies.teleport;

import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.shank.service.AbstractService;
import org.shank.service.lifecycle.LifecycleContext;

/**
 * Represents a TeleportService
 */
class TeleportService extends AbstractService {

    private final BukkitScheduler scheduler;
    private final Plugin plugin;
    private final TeleportController controller;

    private final Logger logger;

    @Inject
    public TeleportService(BukkitScheduler scheduler, Plugin plugin, TeleportController controller, Logger logger) {
        this.scheduler = scheduler;
        this.plugin = plugin;
        this.controller = controller;
        this.logger = logger;
    }

    @Override
    public void start(LifecycleContext context) throws Exception {
        logger.info("Starting teleport task...");
        scheduler.scheduleSyncRepeatingTask(plugin, controller, 0L, 20L);
    }
}
