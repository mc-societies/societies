package org.societies.sieging.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.jooq.Insert;
import org.societies.api.sieging.City;
import org.societies.api.sieging.CityPublisher;
import org.societies.bridge.Location;
import org.societies.groups.group.Group;

import java.util.concurrent.Callable;

/**
 * Represents a SQLCityPublisher
 */
class SQLCityPublisher implements CityPublisher {

    private final ListeningExecutorService service;
    private final SiegingQueries queries;

    public SQLCityPublisher(ListeningExecutorService service, SiegingQueries queries) {
        this.service = service;
        this.queries = queries;
    }

    @Override
    public ListenableFuture<City> publish(final City city, final Group group) {
        return service.submit(new Callable<City>() {
            @Override
            public City call() throws Exception {
                Insert query = queries.getQuery(SiegingQueries.INSERT_CITY);

                query.bind(1, city.getUUID());
                query.bind(2, group.getUUID());

                Location location = city.getLocation();
                query.bind(3, location.getRoundedX());
                query.bind(4, location.getRoundedY());
                query.bind(5, location.getRoundedZ());

                return city;
            }
        });
    }
}