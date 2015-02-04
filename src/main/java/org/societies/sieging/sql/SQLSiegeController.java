package org.societies.sieging.sql;

import com.google.inject.Inject;
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
import org.societies.sieging.wager.EmptyWager;

import javax.inject.Provider;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a SQLSiegeController
 */
class SQLSiegeController implements SiegeController {


    private final Map<UUID, Wager> wagers;

    private final GroupProvider groupProvider;
    private final CityProvider cityProvider;

    private final SiegingQueries queries;

    private final Provider<UUID> provider;

    @Inject
    SQLSiegeController(Map<UUID, Wager> wagers,
                       GroupProvider groupProvider, CityProvider cityProvider,
                       SiegingQueries queries, Provider<UUID> provider) {

        this.queries = queries;
        this.wagers = wagers;
        this.groupProvider = groupProvider;
        this.cityProvider = cityProvider;
        this.provider = provider;
    }

    public Siege start(final Siege siege) {

        Insert query = queries.getQuery(SiegingQueries.INSERT_SIEGE);

        Location location = siege.getLocationInitiated();

        query.bind(1, siege.getUUID());
        query.bind(2, siege.getBesieger().getGroup().getUUID());
        query.bind(3, siege.getCity().getUUID());
        query.bind(4, (short) location.getRoundedX());
        query.bind(5, (short) location.getRoundedY());
        query.bind(6, (short) location.getRoundedZ());
        query.bind(7, siege.getWager().getUUID());

        query.execute();

        return siege;
    }

    @Override
    public Siege start(Besieger besieger, City city, Location location) {
        return start(besieger, city, location, new EmptyWager());
    }

    @Override
    public Siege start(Besieger besieger, City city, Location location, Wager wager) {
        return start(new SQLSiege(provider.get(), besieger, city, location, DateTime.now(), wager));
    }

    @Override
    public Siege getSiege(final UUID uuid) {

        Select<SiegesRecord> query = queries.getQuery(SiegingQueries.SELECT_SIEGE_BY_SIEGE);
        query.bind(1, uuid);
        return newSiege(query.fetchOne());
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
    public Set<Siege> getSieges(final City city) {
        Select<SiegesRecord> query = queries.getQuery(SiegingQueries.SELECT_SIEGES_BY_CITY);
        query.bind(1, city.getUUID());

        return getSieges(query);
    }

    @Override
    public Set<Siege> getSieges(final Besieger besieger) {

        Select<SiegesRecord> query = queries.getQuery(SiegingQueries.SELECT_SIEGES_BY_BESIEGER);
        query.bind(1, besieger.getUUID());

        return getSieges(query);
    }

    @Override
    public Set<Siege> getSiegesAgainst(final Group victim) {

        THashSet<Siege> sieges = new THashSet<Siege>();

        Select<Record8<UUID, UUID, UUID, Short, Short, Short, UUID, DateTime>> query = queries
                .getQuery(SiegingQueries.SELECT_SIEGES_BY_SIEGED);

        query.bind(1, victim.getUUID());

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

    @Override
    public void stop(Siege siege) {
        Delete<?> query = queries.getQuery(SiegingQueries.DROP_SIEGE_BY_SIEGE);
        query.bind(1, siege.getUUID());
    }
}
