package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.publisher.GroupPublisher;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.database.layout.tables.records.SocietiesRecord;
import org.bukkit.ChatColor;
import org.jooq.Insert;

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
