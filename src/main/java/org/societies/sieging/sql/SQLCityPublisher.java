package org.societies.sieging.sql;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.jooq.Insert;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.City;
import org.societies.api.sieging.CityPublisher;
import org.societies.bridge.Location;

import java.util.UUID;

/**
 * Represents a SQLCityPublisher
 */
class SQLCityPublisher implements CityPublisher {

    private final SiegingQueries queries;
    private final Provider<UUID> provider;

    @Inject
    public SQLCityPublisher(SiegingQueries queries, Provider<UUID> provider) {
        this.queries = queries;
        this.provider = provider;
    }

    @Override
    public City publish(String name, Location cityLocation, Besieger group) {
        return publish(new SQLCity(queries, name, provider.get(), cityLocation, group), group);
    }

    @Override
    public City publish(final City city, final Besieger besieger) {
        Insert query = queries.getQuery(SiegingQueries.INSERT_CITY);

        Location location = city.getLocation();

        query.bind(1, city.getUUID())
                .bind(2, besieger.getUUID())
                .bind(3, city.getName())
                .bind(4, location.getRoundedX())
                .bind(5, location.getRoundedY())
                .bind(6, location.getRoundedZ());

        query.execute();

        return city;
    }
}
