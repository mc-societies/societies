package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.publisher.GroupNamePublisher;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.database.sql.layout.tables.records.SocietiesRecord;
import org.jooq.Update;

/**
 * Represents a SocietyNameUpdater
 */
class SQLNamePublisher extends AbstractPublisher implements GroupNamePublisher {

    @Inject
    public SQLNamePublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public void publish(Group group, String name) {
        Update<SocietiesRecord> query = queries.getQuery(SQLQueries.UPDATE_SOCIETY_NAME);

        query.bind(1, name);
        query.bind(2, UUIDGen.toByteArray(group.getUUID()));

        query.execute();
    }
}
