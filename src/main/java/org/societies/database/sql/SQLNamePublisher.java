package org.societies.database.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.lib.core.uuid.UUIDGen;
import org.jooq.Update;
import org.societies.database.sql.layout.tables.records.SocietiesRecord;
import org.societies.groups.group.Group;
import org.societies.groups.publisher.GroupNamePublisher;

import java.util.concurrent.Callable;

/**
 * Represents a SocietyNameUpdater
 */
class SQLNamePublisher extends AbstractPublisher implements GroupNamePublisher {

    @Inject
    public SQLNamePublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public ListenableFuture<Group> publishName(final Group group, final String name) {
        return service.submit(new Callable<Group>() {
            @Override
            public Group call() throws Exception {
                Update<SocietiesRecord> query = queries.getQuery(SQLQueries.UPDATE_SOCIETY_NAME);

                query.bind(1, name);
                query.bind(2, UUIDGen.toByteArray(group.getUUID()));

                query.execute();
                return group;
            }
        });
    }

    @Override
    public ListenableFuture<Group> publishTag(final Group group, final String tag) {
        return service.submit(new Callable<Group>() {
            @Override
            public Group call() throws Exception {
                Update<SocietiesRecord> query = queries.getQuery(SQLQueries.UPDATE_SOCIETY_TAG);

                query.bind(1, tag);
                query.bind(2, UUIDGen.toByteArray(group.getUUID()));

                query.execute();
                return group;
            }
        });
    }
}
