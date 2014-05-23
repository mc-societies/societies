package net.catharos.societies.group;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import net.catharos.groups.DefaultGroup;
import net.catharos.groups.Group;
import net.catharos.groups.GroupProvider;
import net.catharos.groups.GroupFactory;
import net.catharos.lib.shank.AbstractModule;

/**
 * Represents a SocietyModule
 */
public class SocietyModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GroupProvider.class).to(GroupCache.class);

        install(new FactoryModuleBuilder()
                .implement(Group.class, DefaultGroup.class)
                .build(GroupFactory.class));
    }
}
