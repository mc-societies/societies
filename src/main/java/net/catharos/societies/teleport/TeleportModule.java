package net.catharos.societies.teleport;

import net.catharos.lib.shank.service.AbstractServiceModule;

/**
 * Represents a TeleportModule
 */
public class TeleportModule extends AbstractServiceModule {

    @Override
    protected void configure() {
        bindService().to(TeleportService.class);
    }
}
