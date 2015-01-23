package org.societies.sieging;

import gnu.trove.set.hash.THashSet;
import net.catharos.lib.core.uuid.UUIDGen;
import org.jooq.Delete;
import org.jooq.Insert;
import org.jooq.Record1;
import org.jooq.Select;
import org.societies.api.sieging.City;
import org.societies.api.sieging.Land;
import org.societies.bridge.Location;
import org.societies.database.sql.Queries;
import org.societies.database.sql.layout.tables.records.LandsRecord;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a SQLCity
 */
public class SQLCity implements City {

    private final Queries queries;
    private final Location location;
    private final UUID uuid;

    public SQLCity(Queries queries, Location location, UUID uuid) {
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
        Insert query = queries.getQuery(Queries.INSERT_LAND);

        query.bind(1, UUIDGen.toByteArray(land.getUUID()));
        query.bind(2, UUIDGen.toByteArray(getUUID()));

        query.execute();
    }

    @Override
    public Set<Land> getLands() {
        THashSet<Land> lands = new THashSet<Land>();
        Select<Record1<byte[]>> query = queries.getQuery(Queries.SELECT_LANDS_BY_CITY);

        for (Record1<byte[]> record : query.fetch()) {
            UUID uuid = UUIDGen.toUUID(record.value1());

            lands.add(new DefaultLand(uuid, this));
        }

        return lands;
    }

    @Override
    public boolean removeLand(UUID uuid) {
        Delete<LandsRecord> query = queries.getQuery(Queries.DROP_LAND);
        query.bind(1, UUIDGen.toByteArray(uuid));
        return query.execute() != 0;
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
