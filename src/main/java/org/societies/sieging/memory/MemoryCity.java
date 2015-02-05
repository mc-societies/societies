package org.societies.sieging.memory;

import gnu.trove.map.hash.THashMap;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.City;
import org.societies.api.sieging.Land;
import org.societies.bridge.Location;

import java.util.Collection;
import java.util.UUID;

/**
 * Represents a MemoryCity
 */
class MemoryCity implements City {

    private final THashMap<UUID, Land> lands = new THashMap<UUID, Land>();
    private final UUID uuid;
    private final String name;
    private final Location location;
    private final Besieger owner;

    public MemoryCity(UUID uuid, String name, Location location, Besieger owner) {
        this.uuid = uuid;
        this.name = name;
        this.location = location;
        this.owner = owner;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Besieger getOwner() {
        return owner;
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

    @Override
    public String toString() {
        return "MemoryCity{" +
                "location=" + location +
                ", name='" + name + '\'' +
                ", uuid=" + uuid +
                '}';
    }
}
