package org.societies.teleport;

import org.shank.service.AbstractServiceModule;

/**
 * Represents a TeleportModule
 */
public class TeleportModule extends AbstractServiceModule {

    private final boolean enabled;

    public TeleportModule(boolean enabled) {this.enabled = enabled;}

    @Override
    protected void configure() {

        if (enabled) {
            bind(Teleporter.class).to(TeleportController.class);
            bindService().to(TeleportService.class);
        } else {
            bind(Teleporter.class).to(DisabledTeleporter.class);
        }
    }
}
