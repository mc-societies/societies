package org.societies.sieging;

import org.societies.api.sieging.City;
import org.societies.api.sieging.Land;

import java.util.UUID;

/**
 * Represents a MemoryLand
 */
public class DefaultLand implements Land {

    private final UUID uuid;

    private final City origin;

    public DefaultLand(UUID uuid, City origin) {
        this.uuid = uuid;
        this.origin = origin;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public City getOrigin() {
        return origin;
    }
}
