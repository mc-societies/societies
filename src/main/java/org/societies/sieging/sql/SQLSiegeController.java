package org.societies.sieging.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import gnu.trove.set.hash.THashSet;
import org.joda.time.DateTime;
import org.jooq.Delete;
import org.jooq.Insert;
import org.jooq.Record8;
import org.jooq.Select;
import org.societies.api.sieging.*;
import org.societies.bridge.Location;
import org.societies.database.sql.layout.tables.records.SiegesRecord;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupProvider;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Represents a SQLSiegeController
 */
class SQLSiegeController implements SiegeController {


    private final SiegeFactory siegeFactory;
    private final Map<UUID, Wager> wagers;

    private final GroupProvider groupProvider;
    private final CityProvider cityProvider;

    private final SiegingQueries queries;
    private final ListeningExecutorService service;

    SQLSiegeController(SiegeFactory siegeFactory,
                       Map<UUID, Wager> wagers,
                       GroupProvider groupProvider, CityProvider cityProvider,
                       SiegingQueries queries, ListeningExecutorService service) {
        this.queries = queries;
        this.siegeFactory = siegeFactory;
        this.wagers = wagers;
        this.groupProvider = groupProvider;
        this.cityProvider = cityProvider;
        this.service = service;
    }

    public ListenableFuture<Siege> start(final Siege siege) {
        return service.submit(new Callable<Siege>() {
            @Override
            public Siege call() throws Exception {
                Insert query = queries.getQuery(SiegingQueries.INSERT_SIEGE);

                Location location = siege.getLocationInitiated();

                query.bind("uuid", siege.getUUID());
                query.bind("city", siege.getCity().getUUID());
                query.bind("society", siege.getBesieger().getGroup().getUUID());
                query.bind("x", (short) location.getRoundedX());
                query.bind("y", (short) location.getRoundedY());
                query.bind("z", (short) location.getRoundedZ());
                query.bind("wager", siege.getWager().getUUID());

                query.execute();

                return siege;
            }
        });
    }

    @Override
    public ListenableFuture<Siege> start(Group group, City city, Location location) {
        return start(siegeFactory.create(group, city, location));
    }

    @Override
    public ListenableFuture<Siege> start(Group group, City city, Location location, Wager wager) {
        return start(siegeFactory.create(group, city, location, wager));
    }

    @Override
    public ListenableFuture<Siege> getSiege(final UUID uuid) {
        return service.submit(new Callable<Siege>() {
            @Override
            public Siege call() throws Exception {
                Select<SiegesRecord> query = queries.getQuery(SiegingQueries.SELECT_SIEGE_BY_SIEGE);
                query.bind("uuid", uuid);
                return newSiege(query.fetchOne());
            }
        });
    }

    private Siege newSiege(SiegesRecord record) {
        Wager wager = wagers.get(record.getWager());

        Location location = new Location(null, record.getX(), record.getY(), record.getZ());

        Group group = groupProvider.getGroup(record.getSociety());
        City city = cityProvider.getCity(record.getCity());

        return new SQLSiege(
                record.getUuid(),
                group.get(Besieger.class), city,
                location, record.getCreated(), wager
        );
    }

    private Set<Siege> getSieges(Select<SiegesRecord> query) {
        THashSet<Siege> sieges = new THashSet<Siege>();

        for (SiegesRecord record : query.fetch()) {
            sieges.add(newSiege(record));
        }

        return sieges;
    }

    @Override
    public ListenableFuture<Set<Siege>> getSieges(final City city) {
        return service.submit(new Callable<Set<Siege>>() {
            @Override
            public Set<Siege> call() throws Exception {
                Select<SiegesRecord> query = queries.getQuery(SiegingQueries.SELECT_SIEGES_BY_CITY);
                query.bind("city", city.getUUID());

                return getSieges(query);
            }
        });

    }

    @Override
    public ListenableFuture<Set<Siege>> getSieges(final Group besieger) {
        return service.submit(new Callable<Set<Siege>>() {
            @Override
            public Set<Siege> call() throws Exception {
                Select<SiegesRecord> query = queries.getQuery(SiegingQueries.SELECT_SIEGES_BY_BESIEGER);
                query.bind("besieger", besieger.getUUID());

                return getSieges(query);
            }
        });
    }

    @Override
    public ListenableFuture<Set<Siege>> getSiegesAgainst(final Group victim) {
        return service.submit(new Callable<Set<Siege>>() {
            @Override
            public Set<Siege> call() throws Exception {
                THashSet<Siege> sieges = new THashSet<Siege>();

                Select<Record8<UUID, UUID, UUID, Short, Short, Short, UUID, DateTime>> query = queries
                        .getQuery(SiegingQueries.SELECT_SIEGES_BY_SIEGED);

                query.bind("sieged", victim.getUUID());

                for (Record8<UUID, UUID, UUID, Short, Short, Short, UUID, DateTime> record : query.fetch()) {
                    Wager wager = wagers.get(record.value7());
                    Location location = new Location(null, record.value4(), record.value5(), record.value6());

                    Group group = groupProvider.getGroup(record.value2());
                    City city = cityProvider.getCity(record.value3());

                    sieges.add(new SQLSiege(
                            record.value1(),
                            group.get(Besieger.class), city,
                            location, record.value8(), wager));
                }

                return sieges;
            }
        });
    }

    @Override
    public void stop(Siege siege) {
        Delete<?> query = queries.getQuery(SiegingQueries.DROP_SIEGE_BY_SIEGE);
        query.bind("uuid", siege.getUUID());
    }
}
