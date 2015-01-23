package org.societies.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.lib.core.uuid.UUIDGen;
import org.jooq.Query;
import org.societies.database.sql.AbstractPublisher;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupDestructor;

import java.util.concurrent.Callable;

/**
 * Represents a SQLGroupDropPublisher
 */
class SQLGroupDestructor extends AbstractPublisher implements GroupDestructor {

    @Inject
    public SQLGroupDestructor(ListeningExecutorService service, Queries queries) {
        super(service, queries);
    }

    @Override
    public ListenableFuture<Group> destruct(final Group group) {
        return service.submit(new Callable<Group>() {
            @Override
            public Group call() throws Exception {
                Query query = queries.getQuery(Queries.DROP_SOCIETY_BY_UUID);

                query.bind(1, UUIDGen.toByteArray(group.getUUID()));

                query.execute();
                return group;
            }
        });
    }
}
