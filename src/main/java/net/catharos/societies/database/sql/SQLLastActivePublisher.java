package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.publisher.LastActivePublisher;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.database.layout.tables.records.SocietiesRecord;
import org.joda.time.DateTime;
import org.jooq.Update;

import java.sql.Timestamp;

/**
 * Represents a SocietyNameUpdater
 */
class SQLLastActivePublisher extends AbstractPublisher implements LastActivePublisher {

    @Inject
    public SQLLastActivePublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public void publish(Group group, DateTime date) {
        Update<SocietiesRecord> query = queries.getQuery(SQLQueries.UPDATE_SOCIETY_LAST_ACTIVE);

        query.bind(1, UUIDGen.toByteArray(group.getUUID()));
        query.bind(2, new Timestamp(group.getLastActive().getMillis()));

        query.execute();

    }
}
