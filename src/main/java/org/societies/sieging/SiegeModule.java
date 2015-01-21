package org.societies.sieging;

import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import org.shank.AbstractModule;
import org.societies.groups.Extensible;
import org.societies.groups.ExtensionRoller;

/**
 * Represents a SiegeModule
 */
public class SiegeModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<ExtensionRoller> extensions = Multibinder.newSetBinder(binder(), ExtensionRoller.class, Names
                .named("group"));


        extensions.addBinding().toInstance(new ExtensionRoller() {
            @Override
            public void roll(Extensible extensible) {
                extensible.add(new MemoryBesieger());
            }
        });
    }
}
