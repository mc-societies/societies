package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.publisher.GroupCreatedPublisher;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.database.layout.tables.records.MembersRecord;
import org.joda.time.DateTime;
import org.jooq.Update;

import java.sql.Timestamp;

/**
 * Represents a SQLCreatedPublisher
 */
class SQLGroupCreatedPublisher extends AbstractPublisher implements GroupCreatedPublisher {

    @Inject
    public SQLGroupCreatedPublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public void publish(Group group, DateTime created) {
        Update<MembersRecord> query = queries.getQuery(SQLQueries.UPDATE_MEMBER_LAST_ACTIVE);

        query.bind(1, UUIDGen.toByteArray(group.getUUID()));
        query.bind(2, new Timestamp(created.getMillis()));

        query.execute();
    }
}
