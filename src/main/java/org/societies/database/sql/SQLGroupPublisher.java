package org.societies.database.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.lib.core.uuid.UUIDGen;
import org.jooq.Insert;
import org.societies.bridge.ChatColor;
import org.societies.database.sql.layout.tables.records.SocietiesRecord;
import org.societies.groups.group.Group;
import org.societies.groups.publisher.GroupPublisher;

import java.sql.Timestamp;
import java.util.concurrent.Callable;

/**
 * Represents a SQLGroupPublisher
 */
class SQLGroupPublisher extends AbstractPublisher implements GroupPublisher {

    @Inject
    public SQLGroupPublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public ListenableFuture<Group> publish(final Group group) {
        return service.submit(new Callable<Group>() {
            @Override
            public Group call() throws Exception {
                Insert<SocietiesRecord> query = queries.getQuery(SQLQueries.INSERT_SOCIETY);

                query.bind(1, UUIDGen.toByteArray(group.getUUID()));

                query.bind(2, group.getName());
                query.bind(3, group.getTag());
                query.bind(4, ChatColor.stripColor(group.getTag()));
                query.bind(5, new Timestamp(group.getCreated().getMillis()));

                int inserted = query.execute();

                //already exists
                if (inserted == 0) {
                    return null;
                }

                return group;
            }
        });
    }
}