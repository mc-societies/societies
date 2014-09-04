package net.catharos.societies.teleport;

import com.google.inject.Inject;
import net.catharos.lib.shank.service.AbstractService;
import net.catharos.lib.shank.service.lifecycle.LifecycleContext;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Represents a TeleportService
 */
public class TeleportService extends AbstractService {

    private final BukkitScheduler scheduler;
    private final TeleportController controller;
    private final Plugin plugin;

    @Inject
    public TeleportService(BukkitScheduler scheduler, TeleportController controller, Plugin plugin) {
        this.scheduler = scheduler;
        this.controller = controller;
        this.plugin = plugin;
    }

    @Override
    public void start(LifecycleContext context) throws Exception {
        scheduler.scheduleSyncRepeatingTask(plugin, controller, 20L, 20L);
    }
}
