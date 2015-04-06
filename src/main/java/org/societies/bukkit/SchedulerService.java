package org.societies.bukkit;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.shank.service.AbstractService;
import org.shank.service.lifecycle.LifecycleContext;

/**
 * Represents a SchedulerService
 */
public class SchedulerService extends AbstractService {

    private final BukkitScheduler scheduler;
    private final Plugin plugin;
    private final Config config;

    @Inject
    public SchedulerService(BukkitScheduler scheduler, Plugin plugin, Config config) {
        this.scheduler = scheduler;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public void start(LifecycleContext context) throws Exception {
        if (config.getBoolean("city.enable")) {
            scheduler.scheduleSyncRepeatingTask(plugin, context.get(SlowBuffTask.class), 0, 20);
        }
    }
}
