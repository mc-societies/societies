package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.publisher.GroupCreatedPublisher;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.database.sql.layout.tables.records.MembersRecord;
import org.joda.time.DateTime;
import org.jooq.Update;

import java.sql.Timestamp;
import java.util.concurrent.Callable;

/**
 * Represents a SQLCreatedPublisher
 */
class SQLGroupCreatedPublisher extends AbstractPublisher implements GroupCreatedPublisher {

    @Inject
    public SQLGroupCreatedPublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public ListenableFuture<Group> publishCreated(final Group group, final DateTime created) {
        return service.submit(new Callable<Group>() {
            @Override
            public Group call() throws Exception {
                Update<MembersRecord> query = queries.getQuery(SQLQueries.UPDATE_MEMBER_LAST_ACTIVE);

                query.bind(1, UUIDGen.toByteArray(group.getUUID()));
                query.bind(2, new Timestamp(created.getMillis()));

                query.execute();

                return group;
            }
        });
    }
}
