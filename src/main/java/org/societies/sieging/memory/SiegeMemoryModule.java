package org.societies.sieging.memory;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.shank.AbstractModule;
import org.societies.api.sieging.*;
import org.societies.groups.ExtensionRoller;
import org.societies.groups.group.Group;
import org.societies.sieging.DefaultBesiegerProvider;
import org.societies.sieging.DefaultBesiegerPublisher;

/**
 * Represents a SiegeMemoryModule
 */
public class SiegeMemoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(BesiegerProvider.class).to(DefaultBesiegerProvider.class);
        bind(BesiegerPublisher.class).to(DefaultBesiegerPublisher.class);
        bind(SiegeController.class).to(MemorySiegeController.class);


        Key<MemoryCityController> city = Key.get(MemoryCityController.class);

        bind(CityProvider.class).to(city);
        bind(CityPublisher.class).to(city);

        Multibinder<ExtensionRoller<Group>> extensions = Multibinder
                .newSetBinder(binder(), new TypeLiteral<ExtensionRoller<Group>>() {});


        extensions.addBinding().toInstance(new ExtensionRoller<Group>() {
            @Override
            public void roll(Group extensible) {
                extensible.add(Besieger.class, new MemoryBesieger(extensible));
            }
        });
    }
}
