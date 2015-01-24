package org.societies.sieging.sql;

import gnu.trove.set.hash.THashSet;
import org.jooq.Delete;
import org.jooq.Insert;
import org.jooq.Select;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.City;
import org.societies.bridge.Location;
import org.societies.database.sql.layout.tables.records.CitiesRecord;
import org.societies.groups.group.Group;

import java.util.Set;

/**
 * Represents a SQLBesieger
 */
class SQLBesieger implements Besieger {

    private final Group group;
    private final SiegingQueries queries;

    public SQLBesieger(Group group, SiegingQueries queries) {
        this.group = group;
        this.queries = queries;
    }

    @Override
    public Group getGroup() {
        return group;
    }

    @Override
    public void addCity(City city) {
        Insert query = queries.getQuery(SiegingQueries.INSERT_CITY);

        query.bind(1, city.getUUID());
        query.bind(2, group.getUUID());

        query.bind(3, city.getLocation().getRoundedX());
        query.bind(4, city.getLocation().getRoundedY());
        query.bind(5, city.getLocation().getRoundedZ());


        query.execute();
    }

    @Override
    public void removeCity(City city) {
        Delete<CitiesRecord> query = queries.getQuery(SiegingQueries.DROP_CITY);
        query.bind(1, city.getUUID());
        query.execute();
    }

    @Override
    public Set<City> getCities() {
        THashSet<City> cities = new THashSet<City>();
        Select<CitiesRecord> query = queries.getQuery(SiegingQueries.SELECT_CITIES_BY_SOCIETY);

        for (CitiesRecord record : query.fetch()) {
            SQLCity city = new SQLCity(queries, new Location(null, record.getX(), record.getY(), record.getZ()), record.getUuid());
            cities.add(city);
        }

        return cities;
    }
}
