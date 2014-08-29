package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.publisher.GroupStatePublisher;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.database.layout.tables.records.SocietiesRecord;
import org.jooq.Update;

/**
 * Represents a SQLGroupStatePublisher
 */
class SQLGroupStatePublisher extends AbstractPublisher implements GroupStatePublisher {

    @Inject
    public SQLGroupStatePublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public void publish(Group group, short state) {
        Update<SocietiesRecord> query = queries.getQuery(SQLQueries.UPDATE_SOCIETY_STATE);

        query.bind(1, state);
        query.bind(2, UUIDGen.toByteArray(group.getUUID()));

        query.execute();
    }
}
