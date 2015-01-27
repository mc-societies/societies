package org.societies.sieging.sql;

import org.joda.time.DateTime;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.City;
import org.societies.api.sieging.Siege;
import org.societies.api.sieging.Wager;
import org.societies.bridge.Location;

import java.util.UUID;

/**
 * Represents a SQLSiege
 */
public class SQLSiege implements Siege {


    private final UUID uuid;

    private final Besieger besieger;
    private final City city;

    private final Location location;
    private final DateTime timeInitiated;
    private final Wager wager;


    public SQLSiege(UUID uuid, Besieger besieger, City city, Location location, DateTime timeInitiated, Wager wager) {
        this.uuid = uuid;
        this.besieger = besieger;
        this.city = city;
        this.location = location;
        this.timeInitiated = timeInitiated;
        this.wager = wager;
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
        return timeInitiated;
    }
}
