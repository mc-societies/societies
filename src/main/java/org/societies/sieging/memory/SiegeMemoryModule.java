package org.societies.sieging.memory;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.shank.service.AbstractServiceModule;
import org.societies.api.sieging.*;
import org.societies.groups.ExtensionRoller;
import org.societies.groups.group.Group;

/**
 * Represents a SiegeMemoryModule
 */
public class SiegeMemoryModule extends AbstractServiceModule {
    @Override
    protected void configure() {
        bind(BesiegerProvider.class).to(ForwardingBesiegerProvider.class);
        bind(BesiegerPublisher.class).to(ForwardingBesiegerPublisher.class);
        bind(SiegeController.class).to(MemorySiegeController.class);


        Key<MemoryCityController> city = Key.get(MemoryCityController.class);

        bind(CityProvider.class).to(city);
        bind(CityPublisher.class).to(city);

        bindService().to(city);

        Multibinder<ExtensionRoller<Group>> extensions = Multibinder
                .newSetBinder(binder(), new TypeLiteral<ExtensionRoller<Group>>() {});


        extensions.addBinding().to(MemorySiegeRoller.class);
    }

}
