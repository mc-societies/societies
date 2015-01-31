package org.societies.sieging.sql;

import org.jooq.Insert;
import org.societies.api.sieging.City;
import org.societies.api.sieging.CityPublisher;
import org.societies.bridge.Location;
import org.societies.groups.group.Group;

/**
 * Represents a SQLCityPublisher
 */
class SQLCityPublisher implements CityPublisher {

    private final SiegingQueries queries;

    public SQLCityPublisher(SiegingQueries queries) {
        this.queries = queries;
    }

    @Override
    public City publish(final City city, final Group group) {
        Insert query = queries.getQuery(SiegingQueries.INSERT_CITY);

        query.bind(1, city.getUUID());
        query.bind(2, group.getUUID());

        Location location = city.getLocation();
        query.bind(3, location.getRoundedX());
        query.bind(4, location.getRoundedY());
        query.bind(5, location.getRoundedZ());

        return city;
    }
}
