package org.societies.util.uuid;

import javax.inject.Provider;
import java.util.UUID;

/**
 * Represents a RandomUUIDProvider
 */
public class RandomUUIDProvider implements Provider<UUID> {

    @Override
    public UUID get() {
        return UUID.randomUUID();
    }
}
