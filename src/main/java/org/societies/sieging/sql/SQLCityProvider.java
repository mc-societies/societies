package org.societies.sieging.sql;

import com.google.inject.Inject;
import org.jooq.Result;
import org.jooq.Select;
import org.societies.api.sieging.BesiegerProvider;
import org.societies.api.sieging.City;
import org.societies.api.sieging.CityProvider;
import org.societies.bridge.Location;
import org.societies.database.sql.layout.tables.records.CitiesRecord;

import java.util.UUID;

import static org.societies.sieging.sql.SiegingQueries.SELECT_NEAREST;

/**
 * Represents a SQLCityProvider
 */
class SQLCityProvider implements CityProvider {

    private final SiegingQueries queries;
    private final BesiegerProvider besiegerProvider;

    @Inject
    public SQLCityProvider(SiegingQueries queries, BesiegerProvider besiegerProvider) {
        this.queries = queries;
        this.besiegerProvider = besiegerProvider;
    }

    @Override
    public City getCity(UUID uuid) {
        return toCity(queries.getQuery(SiegingQueries.SELECT_CITY_BY_UUID).bind(1, uuid).fetchOne());
    }

    @Override
    public City getCity(String name) {
        return toCity(queries.getQuery(SiegingQueries.SELECT_CITY_BY_NAME).bind(1, name).fetchOne());
    }

    @Override
    public City getCity(Location location) {
        Select<CitiesRecord> query = queries.getQuery(SELECT_NEAREST);

        query.bind(1, location.getRoundedX());
        query.bind(2, 2);
        query.bind(3, location.getRoundedY());
        query.bind(4, 2);
        query.bind(5, location.getRoundedZ());
        query.bind(6, 2);

        Result<CitiesRecord> result = query.fetch();

        if (result.size() != 1) {
            return null;
        }

        return toCity(result.get(0));
    }

    City toCity(CitiesRecord record) {
        if (record == null) {
            return null;
        }

        return new SQLCity(queries, record.getName(), record.getUuid(), new Location(null, record.getX(), record.getY(), record
                .getZ()), besiegerProvider.getBesieger(record.getSociety()));
    }
}
