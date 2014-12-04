package org.societies.teleport;

import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.shank.logging.InjectLogger;
import org.shank.service.AbstractService;
import org.shank.service.lifecycle.LifecycleContext;
import org.societies.bridge.Scheduler;

/**
 * Represents a TeleportService
 */
public class TeleportService extends AbstractService {

    private final Scheduler scheduler;
    private final TeleportController controller;

    @InjectLogger
    private Logger logger;

    @Inject
    public TeleportService(Scheduler scheduler, TeleportController controller) {
        this.scheduler = scheduler;
        this.controller = controller;
    }

    @Override
    public void start(LifecycleContext context) throws Exception {
        logger.info("Starting teleport task...");
        scheduler.scheduleSyncRepeatingTask(controller, 20L, 20L);
    }
}
