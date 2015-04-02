package org.societies.util.uuid;

import javax.inject.Provider;
import java.util.UUID;

/**
 * Represents a TimeUUIDProvider
 */
public class TimeUUIDProvider implements Provider<UUID> {
    private static final UUIDGen GENERATOR = new UUIDGen();


    @Override
    public UUID get() {
        return GENERATOR.generateUUID1();
    }
}
