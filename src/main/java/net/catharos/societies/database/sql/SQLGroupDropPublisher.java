package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.publisher.GroupDropPublisher;
import net.catharos.lib.core.uuid.UUIDGen;
import org.jooq.Query;

import java.util.concurrent.Callable;

/**
 * Represents a SQLGroupDropPublisher
 */
class SQLGroupDropPublisher extends AbstractPublisher implements GroupDropPublisher {

    @Inject
    public SQLGroupDropPublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public ListenableFuture<Group> drop(final Group group) {
        return service.submit(new Callable<Group>() {
            @Override
            public Group call() throws Exception {
                Query query = queries.getQuery(SQLQueries.DROP_SOCIETY_BY_UUID);

                query.bind(1, UUIDGen.toByteArray(group.getUUID()));

                query.execute();
                return group;
            }
        });
    }
}
