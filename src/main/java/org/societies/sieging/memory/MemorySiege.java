package org.societies.sieging.memory;

import org.joda.time.DateTime;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.City;
import org.societies.api.sieging.Siege;
import org.societies.api.sieging.Wager;
import org.societies.bridge.Location;

import java.util.UUID;

/**
 * Represents a MemorySiege
 */
class MemorySiege implements Siege {

    private final UUID uuid;
    private final Besieger besieger;
    private final City city;
    private final Wager wager;
    private final Location location;
    private final DateTime time;

    public MemorySiege(UUID uuid,
                       Besieger besieger, City city,
                       Wager wager,
                       Location location, DateTime time) {
        this.uuid = uuid;
        this.besieger = besieger;
        this.city = city;
        this.wager = wager;
        this.location = location;
        this.time = time;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public Besieger getBesieger() {
        return besieger;
    }

    @Override
    public City getCity() {
        return city;
    }

    @Override
    public Wager getWager() {
        return wager;
    }

    @Override
    public Location getLocationInitiated() {
        return location;
    }

    @Override
    public DateTime getTimeInitiated() {
        return time;
    }
}
