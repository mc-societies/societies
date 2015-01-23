package org.societies.sieging;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.shank.AbstractModule;
import org.societies.groups.ExtensionRoller;
import org.societies.groups.group.Group;

/**
 * Represents a SiegeModule
 */
public class SiegeModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<ExtensionRoller<Group>> extensions = Multibinder
                .newSetBinder(binder(), new TypeLiteral<ExtensionRoller<Group>>() {});


//        extensions.addBinding().toInstance(new ExtensionRoller<Group>() {
//            @Override
//            public void roll(Group extensible) {
//                extensible.add(new MemoryBesieger(extensible));
//            }
//        });
    }
}
