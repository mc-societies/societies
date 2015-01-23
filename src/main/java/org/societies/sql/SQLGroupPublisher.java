package org.societies.sql;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.lib.core.uuid.UUIDGen;
import org.joda.time.DateTime;
import org.jooq.Insert;
import org.societies.bridge.ChatColor;
import org.societies.database.sql.AbstractPublisher;
import org.societies.database.sql.layout.tables.records.SocietiesRecord;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupFactory;
import org.societies.groups.group.GroupPublisher;

import javax.inject.Provider;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Represents a SQLGroupPublisher
 */
class SQLGroupPublisher extends AbstractPublisher implements GroupPublisher {

    private final Provider<UUID> uuid;
    private final GroupFactory groupFactory;

    @Inject
    public SQLGroupPublisher(Provider<UUID> uuid, ListeningExecutorService service, Queries queries, GroupFactory groupFactory) {
        super(service, queries);
        this.uuid = uuid;
        this.groupFactory = groupFactory;
    }

    @Override
    public ListenableFuture<Group> publish(final UUID uuid, final String name, final String tag, final DateTime created) {
        return service.submit(new Callable<Group>() {
            @Override
            public Group call() throws Exception {
                Group group = groupFactory.create(uuid, name, tag, created);

                Insert<SocietiesRecord> query = queries.getQuery(Queries.INSERT_SOCIETY);

                query.bind(1, UUIDGen.toByteArray(uuid));
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
        });
    }

    @Override
    public ListenableFuture<Group> publish(String name, String tag) {
        return publish(uuid.get(), name, tag, DateTime.now());
    }

    @Override
    public ListenableFuture<Group> publish(Group group) {
        return Futures.immediateFuture(group);
    }
}
