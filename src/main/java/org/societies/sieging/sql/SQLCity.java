package org.societies.sieging.sql;

import gnu.trove.set.hash.THashSet;
import org.jooq.Delete;
import org.jooq.Insert;
import org.jooq.Record1;
import org.jooq.Select;
import org.societies.api.sieging.City;
import org.societies.api.sieging.Land;
import org.societies.bridge.Location;
import org.societies.database.sql.layout.tables.records.LandsRecord;
import org.societies.sieging.DefaultLand;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a SQLCity
 */
class SQLCity implements City {

    private final SiegingQueries queries;
    private final Location location;
    private final UUID uuid;

    public SQLCity(SiegingQueries queries, Location location, UUID uuid) {
        this.queries = queries;
        this.location = location;
        this.uuid = uuid;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void addLand(Land land) {
        Insert query = queries.getQuery(SiegingQueries.INSERT_LAND);

        query.bind(1, land.getUUID());
        query.bind(2, getUUID());

        query.execute();
    }

    @Override
    public Set<Land> getLands() {
        THashSet<Land> lands = new THashSet<Land>();
        Select<Record1<UUID>> query = queries.getQuery(SiegingQueries.SELECT_LANDS_BY_CITY);

        for (Record1<UUID> record : query.fetch()) {
            UUID uuid = record.value1();

            lands.add(new DefaultLand(uuid, this));
        }

        return lands;
    }

    @Override
    public boolean removeLand(UUID uuid) {
        Delete<LandsRecord> query = queries.getQuery(SiegingQueries.DROP_LAND);
        query.bind(1, uuid);
        return query.execute() != 0;
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
