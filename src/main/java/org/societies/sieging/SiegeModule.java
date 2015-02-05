package org.societies.sieging;

import com.google.inject.TypeLiteral;
import org.shank.AbstractModule;
import org.societies.api.sieging.Wager;
import org.societies.sieging.commands.SiegeCommandModule;
import org.societies.sieging.memory.SiegeMemoryModule;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a SiegeModule
 */
public class SiegeModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(new TypeLiteral<Map<UUID, Wager>>() {}).toInstance(Collections.<UUID, Wager>emptyMap());

        install(new SiegeMemoryModule());

        install(new SiegeCommandModule());
    }
}
