package net.catharos.societies.group.sql;

import net.catharos.groups.GroupProvider;
import net.catharos.lib.shank.AbstractModule;

/**
 * Represents a MemberProviderModule
 */
public class GroupProviderModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SocietyQueries.class);

        bind(GroupProvider.class).to(SQLGroupProvider.class);
    }
}
