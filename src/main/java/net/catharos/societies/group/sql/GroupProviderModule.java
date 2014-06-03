package net.catharos.societies.group.sql;

import com.google.inject.Key;
import net.catharos.groups.GroupProvider;
import net.catharos.groups.GroupPublisher;
import net.catharos.lib.shank.AbstractModule;

/**
 * Represents a MemberProviderModule
 */
public class GroupProviderModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SocietyQueries.class);

        Key<SQLGroupController> controller = Key.get(SQLGroupController.class);

        bind(GroupProvider.class).to(controller);
        bind(GroupPublisher.class).to(controller);
    }
}
