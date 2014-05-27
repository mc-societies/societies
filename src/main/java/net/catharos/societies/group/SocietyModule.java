package net.catharos.societies.group;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import net.catharos.groups.DefaultGroup;
import net.catharos.groups.Group;
import net.catharos.groups.GroupFactory;
import net.catharos.lib.shank.AbstractModule;
import net.catharos.societies.group.sql.GroupProviderModule;

/**
 * Represents a SocietyModule
 */
public class SocietyModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(Group.class, DefaultGroup.class)
                .build(GroupFactory.class));


        bind(Group.class).to(DefaultGroup.class);

        install(new GroupProviderModule());
    }
}
