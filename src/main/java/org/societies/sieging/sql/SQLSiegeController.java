package org.societies.sieging.sql;

import org.jooq.Insert;
import org.societies.api.sieging.City;
import org.societies.api.sieging.Siege;
import org.societies.api.sieging.SiegeController;
import org.societies.api.sieging.Wager;
import org.societies.bridge.Location;
import org.societies.groups.group.Group;

import java.util.Set;

/**
 * Represents a SQLSiegeController
 */
class SQLSiegeController implements SiegeController {

    private final SiegingQueries queries;

    SQLSiegeController(SiegingQueries queries) {
        this.queries = queries;
    }

    @Override
    public Siege start(Group group, City city, Location location) {
        Insert query = queries.getQuery(SiegingQueries.INSERT_SIEGE);



        return null;
    }

    @Override
    public Siege start(Group group, City city, Location location, Wager wager) {
        return null;
    }

    @Override
    public Set<Siege> getSieges(City city) {
        return null;
    }

    @Override
    public Set<Siege> getSieges(Group group) {
        return null;
    }

    @Override
    public Set<Siege> getSiegesAgainst(Group group) {
        return null;
    }

    @Override
    public void stop(Group group, City city) {

    }
}