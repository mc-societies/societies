package org.societies.sieging.memory;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.societies.api.math.Location;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.City;
import org.societies.api.sieging.Siege;
import org.societies.api.sieging.Wager;

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
    private final DateTime initiateTime;
    private final DateTime startTime;

    public MemorySiege(UUID uuid,
                       Besieger besieger, City city,
                       Wager wager,
                       Location location, DateTime initiateTime, DateTime startTime) {
        this.uuid = uuid;
        this.besieger = besieger;
        this.city = city;
        this.wager = wager;
        this.location = location;
        this.initiateTime = initiateTime;
        this.startTime = startTime;
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
        return initiateTime;
    }

    @Override
    public DateTime getStartTime() {
        return startTime;
    }

    @Override
    public Duration getDurationUntilStart() {
        return new Duration(initiateTime, startTime);
    }

    @Override
    public boolean isStarted() {
        return DateTime.now().isAfter(getStartTime());
    }

    @Override
    public void send(String message) {
        getBesieger().getGroup().send(message);
        getCity().getOwner().getGroup().send(message);
    }

    @Override
    public void send(String message, Object... obj) {
        getBesieger().getGroup().send(message, obj);
        getCity().getOwner().getGroup().send(message, obj);
    }
}
