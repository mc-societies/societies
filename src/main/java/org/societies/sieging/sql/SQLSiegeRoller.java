package org.societies.sieging.sql;

import com.google.inject.Inject;
import org.societies.api.sieging.Besieger;
import org.societies.groups.ExtensionRoller;
import org.societies.groups.group.Group;

/**
 * Represents a SQLSiegeRoller
 */
class SQLSiegeRoller implements ExtensionRoller<Group> {

    private final SiegingQueries queries;

    @Inject
    SQLSiegeRoller(SiegingQueries queries) {this.queries = queries;}

    @Override
    public void roll(Group extensible) {
        extensible.add(Besieger.class, new SQLBesieger(extensible, queries));
    }
}
