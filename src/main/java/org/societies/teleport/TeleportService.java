package org.societies.teleport;

import com.google.inject.Inject;
import org.societies.bridge.Scheduler;
import net.catharos.lib.shank.service.AbstractService;
import net.catharos.lib.shank.service.lifecycle.LifecycleContext;

/**
 * Represents a TeleportService
 */
public class TeleportService extends AbstractService {

    private final Scheduler scheduler;
    private final TeleportController controller;

    @Inject
    public TeleportService(Scheduler scheduler, TeleportController controller) {
        this.scheduler = scheduler;
        this.controller = controller;
    }

    @Override
    public void start(LifecycleContext context) throws Exception {
        scheduler.scheduleSyncRepeatingTask(controller, 20L, 20L);
    }
}
