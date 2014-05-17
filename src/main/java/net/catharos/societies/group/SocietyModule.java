package net.catharos.societies.group;

import net.catharos.groups.GroupCache;
import net.catharos.lib.shank.AbstractModule;

/**
 * Represents a SocietyModule
 */
public class SocietyModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GroupCache.class).to(LoadingGroupCache.class);
    }
}
