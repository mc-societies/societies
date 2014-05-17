package net.catharos.societies.group;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import net.catharos.groups.GroupCache;
import net.catharos.groups.GroupFactory;
import net.catharos.lib.shank.AbstractModule;

/**
 * Represents a SocietyModule
 */
public class SocietyModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GroupCache.class).to(LoadingGroupCache.class);

        install(new FactoryModuleBuilder()
                .build(GroupFactory.class));
    }
}
