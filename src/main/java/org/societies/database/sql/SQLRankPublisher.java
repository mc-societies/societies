package org.societies.database.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.lib.core.uuid.UUIDGen;
import org.jooq.Insert;
import org.societies.database.sql.layout.tables.records.RanksRecord;
import org.societies.groups.rank.Rank;
import org.societies.groups.rank.RankPublisher;

import java.util.concurrent.Callable;

/**
 * Represents a SQLGroupRankPublisher
 */
//beautify delete
class SQLRankPublisher extends AbstractPublisher implements RankPublisher {

    @Inject
    public SQLRankPublisher(ListeningExecutorService service, Queries queries) {
        super(service, queries);
    }

    @Override
    public ListenableFuture<Rank> publish(final Rank rank) {
        return service.submit(new Callable<Rank>() {
            @Override
            public Rank call() throws Exception {
                byte[] uuid = UUIDGen.toByteArray(rank.getUUID());
                String name = rank.getName();
                int priority = rank.getPriority();

                Insert<RanksRecord> query = queries.getQuery(Queries.INSERT_RANK);

                query.bind(1, uuid);
                query.bind(2, name);
                query.bind(3, priority);
                query.bind(4, uuid);
                query.bind(5, name);
                query.bind(6, priority);
                query.execute();
                return rank;
            }
        });
    }
}
