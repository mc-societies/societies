package net.catharos.societies.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.database.layout.tables.records.SocietiesRecord;
import org.jooq.Update;

import java.util.concurrent.Callable;

/**
 * Represents a SocietyNameUpdater
 */
class NamePublisher extends AbstractPublisher<Group> {

    @Inject
    public NamePublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public ListenableFuture<Group> update(final Group group) {
        return service.submit(new Callable<Group>() {
            @Override
            public Group call() throws Exception {
                Update<SocietiesRecord> query = queries.getQuery(SQLQueries.UPDATE_SOCIETY_NAME);

                query.bind(1, group.getName());
                query.bind(2, UUIDGen.toByteArray(group.getUUID()));

                query.execute();

                return group;
            }
        });
    }
}
