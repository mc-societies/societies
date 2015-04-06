package org.societies.sieging.memory;

import com.google.common.base.Function;
import gnu.trove.map.hash.THashMap;
import org.joda.time.DateTime;
import org.societies.api.math.Location;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.City;
import org.societies.api.sieging.CityPublisher;
import org.societies.api.sieging.Land;

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
    private Besieger owner;
    private final DateTime founded;
    private final CityPublisher cityPublisher;

    private final Function<Integer, Double> cityFunction;

    private boolean linked = false;

    public MemoryCity(UUID uuid, String name,
                      Location location, Besieger owner,
                      DateTime founded,
                      CityPublisher cityPublisher,
                      Function<Integer, Double> cityFunction) {
        this.uuid = uuid;
        this.name = name;
        this.location = location;
        this.owner = owner;
        this.founded = founded;
        this.cityPublisher = cityPublisher;
        this.cityFunction = cityFunction;
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
    public void setOwner(Besieger owner) {
        this.owner = owner;
    }

    @Override
    public void addLand(Land land) {
        lands.put(land.getUUID(), land);

        if (linked()) {
            cityPublisher.publish(this);
        }
    }

    @Override
    public Collection<Land> getLands() {
        return lands.values();
    }

    @Override
    public boolean removeLand(UUID uuid) {
        boolean result = lands.remove(uuid) != null;
        if (linked()) {
            cityPublisher.publish(this);
        }
        return result;
    }

    @Override
    public void clearLands() {
        lands.clear();

        if (linked()) {
            cityPublisher.publish(this);
        }
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public double getRadius() {
        Double radius = cityFunction.apply(lands.size());
        return radius == null ? 0 : radius;
    }

    @Override
    public double distance(Location location) {
        double distance = location.distance(getLocation());
        double radius = getRadius();

        if (distance >= radius) {
            distance -= radius;
        } else {
            return 0;
        }

        return distance;
    }

    @Override
    public DateTime getFounded() {
        return founded;
    }

    @Override
    public String toString() {
        return "MemoryCity{" +
                "location=" + location +
                ", name='" + name + '\'' +
                ", uuid=" + uuid +
                '}';
    }

    @Override
    public boolean linked() {
        return linked;
    }

    @Override
    public void unlink() {
        linked = false;
    }

    @Override
    public void link() {
        linked = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MemoryCity that = (MemoryCity) o;

        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
