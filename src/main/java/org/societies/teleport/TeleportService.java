package org.societies.teleport;

import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.shank.service.AbstractService;
import org.shank.service.lifecycle.LifecycleContext;
import org.societies.bridge.Scheduler;

/**
 * Represents a TeleportService
 */
class TeleportService extends AbstractService {

    private final Scheduler scheduler;
    private final TeleportController controller;

    private final Logger logger;

    @Inject
    public TeleportService(Scheduler scheduler, TeleportController controller, Logger logger) {
        this.scheduler = scheduler;
        this.controller = controller;
        this.logger = logger;
    }

    @Override
    public void start(LifecycleContext context) throws Exception {
        logger.info("Starting teleport task...");
        scheduler.scheduleSyncRepeatingTask(controller, 20L);
    }
}
