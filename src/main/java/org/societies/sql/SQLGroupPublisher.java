package org.societies.sql;

import com.google.inject.Inject;
import org.joda.time.DateTime;
import org.jooq.Insert;
import org.jooq.Query;
import org.societies.bridge.ChatColor;
import org.societies.database.sql.layout.tables.records.SocietiesRecord;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupFactory;
import org.societies.groups.group.GroupPublisher;

import javax.inject.Provider;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Represents a SQLGroupPublisher
 */
class SQLGroupPublisher implements GroupPublisher {

    private final Provider<UUID> uuid;
    private final Queries queries;
    private final GroupFactory groupFactory;

    @Inject
    public SQLGroupPublisher(Provider<UUID> uuid, Queries queries, GroupFactory groupFactory) {
        this.uuid = uuid;
        this.queries = queries;
        this.groupFactory = groupFactory;
    }

    @Override
    public Group publish(final UUID uuid, final String name, final String tag, final DateTime created) {
        Group group = groupFactory.create(uuid, name, tag, created);

        Insert<SocietiesRecord> query = queries.getQuery(Queries.INSERT_SOCIETY);

        query.bind(1, uuid);
        query.bind(2, name);
        query.bind(3, tag);
        query.bind(4, ChatColor.stripColor(tag));
        query.bind(5, new Timestamp(created.getMillis()));


        int inserted = query.execute();

        //already exists
        if (inserted == 0) {
            return null;
        }

        return group;
    }

    @Override
    public Group publish(String name, String tag) {
        return publish(uuid.get(), name, tag, DateTime.now());
    }

    @Override
    public Group publish(Group group) {
        return group;
    }

    @Override
    public Group destruct(final Group group) {
        Query query = queries.getQuery(Queries.DROP_SOCIETY_BY_UUID);

        query.bind(1, group.getUUID());

        query.execute();
        return group;
    }
}
