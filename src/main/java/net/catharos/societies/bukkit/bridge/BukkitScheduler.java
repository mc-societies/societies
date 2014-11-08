package net.catharos.societies.bukkit.bridge;

import com.google.inject.Inject;
import net.catharos.societies.bridge.Scheduler;
import org.bukkit.plugin.Plugin;

/**
 * Represents a BukkitScheduler
 */
public class BukkitScheduler implements Scheduler {

    private final org.bukkit.scheduler.BukkitScheduler scheduler;
    private final Plugin plugin;

    @Inject
    public BukkitScheduler(org.bukkit.scheduler.BukkitScheduler scheduler, Plugin plugin) {
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public void scheduleSyncRepeatingTask(Runnable task, long delay, long period) {
        scheduler.scheduleSyncRepeatingTask(plugin, task, delay, period);
    }
}
