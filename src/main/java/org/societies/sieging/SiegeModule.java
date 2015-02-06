package org.societies.sieging;

import com.google.inject.TypeLiteral;
import net.catharos.lib.core.uuid.UUIDStorage;
import org.shank.AbstractModule;
import org.societies.api.sieging.ActionValidator;
import org.societies.api.sieging.SimpleActionValidator;
import org.societies.api.sieging.Wager;
import org.societies.sieging.commands.SiegeCommandModule;
import org.societies.sieging.memory.SiegeMemoryModule;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a SiegeModule
 */
public class SiegeModule extends AbstractModule {

    private final File root;

    public SiegeModule(File root) {this.root = root;}

    @Override
    protected void configure() {
        bind(new TypeLiteral<Map<UUID, Wager>>() {}).toInstance(Collections.<UUID, Wager>emptyMap());

        install(new SiegeMemoryModule());

        install(new SiegeCommandModule());

        bind(ActionValidator.class).to(SimpleActionValidator.class);

        bindNamed("cities", UUIDStorage.class).toInstance(new UUIDStorage(new File(root, "cities"), "json"));
    }
}
