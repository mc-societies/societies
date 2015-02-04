package org.societies.sieging.sql;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.shank.AbstractModule;
import org.societies.api.sieging.*;
import org.societies.groups.ExtensionRoller;
import org.societies.groups.group.Group;
import org.societies.sieging.DefaultBesiegerProvider;
import org.societies.sieging.DefaultBesiegerPublisher;

/**
 * Represents a SQLModule
 */
public class SQLSiegingModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CityProvider.class).to(SQLCityProvider.class);
        bind(CityPublisher.class).to(SQLCityPublisher.class);


        bind(SiegeController.class).to(SQLSiegeController.class);


        bind(BesiegerProvider.class).to(DefaultBesiegerProvider.class);
        bind(BesiegerPublisher.class).to(DefaultBesiegerPublisher.class);

        Multibinder<ExtensionRoller<Group>> extensions = Multibinder
                .newSetBinder(binder(), new TypeLiteral<ExtensionRoller<Group>>() {});


        extensions.addBinding().to(SQLSiegeRoller.class);
    }
}
