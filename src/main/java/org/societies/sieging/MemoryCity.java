package org.societies.sieging;

import gnu.trove.map.hash.THashMap;
import org.societies.api.sieging.City;
import org.societies.api.sieging.Land;
import org.societies.bridge.Location;

import java.util.Collection;
import java.util.UUID;

/**
 * Represents a MemoryCity
 */
public class MemoryCity implements City {

    private final THashMap<UUID, Land> lands = new THashMap<UUID, Land>();
    private final UUID uuid;
    private final Location location;

    public MemoryCity(UUID uuid, Location location) {
        this.uuid = uuid;
        this.location = location;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void addLand(Land land) {
        lands.put(land.getUUID(), land);
    }

    @Override
    public Collection<Land> getLands() {
        return lands.values();
    }

    @Override
    public boolean removeLand(UUID uuid) {
        return lands.remove(uuid) != null;
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
