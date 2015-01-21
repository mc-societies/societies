package org.societies.sieging;

import org.societies.api.sieging.City;
import org.societies.api.sieging.Land;
import org.societies.bridge.Location;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a MemoryCity
 */
public class MemoryCity implements City {

    @Override
    public void addLand(Land land) {

    }

    @Override
    public Set<Land> getLands() {
        return null;
    }

    @Override
    public boolean removeLand(UUID uuid) {
        return false;
    }

    @Override
    public Location getLocation() {
        return null;
    }
}
