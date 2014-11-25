package net.catharos.societies.database.sql;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.catharos.societies.api.lock.Locker;
import org.jooq.Query;
import org.jooq.Select;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;

/**
 * Represents a SQLLocker
 */
@Singleton
public class SQLLocker extends AbstractPublisher implements Locker {

//todo    private final TIntObjectHashMap<ReentrantLock> locks = new TIntObjectHashMap<ReentrantLock>();

    @Inject
    public SQLLocker(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public ListenableFuture<Boolean> lock(final int id) {
        return service.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Query query = queries.getQuery(SQLQueries.INSERT_LOCK);

                query.bind(1, id);

                return query.execute() > 0;
            }
        });
    }


    @Override
    public ListenableFuture<Boolean> unlock(final int id) {
        return service.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Query query = queries.getQuery(SQLQueries.DROP_LOCK);

                query.bind(1, id);

                return query.execute() > 0;
            }
        });
    }

    @Override
    public ListenableFuture<Boolean> isLocked(final int id) {
        return service.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Select query = queries.getQuery(SQLQueries.SELECT_LOCK);

                query.bind(1, id);

                return query.fetchCount() > 0;
            }
        });
    }

    @Override
    public ListenableFuture<Boolean> isFree(int id) {
        return Futures.transform(isLocked(id), new Function<Boolean, Boolean>() {
            @Nullable
            @Override
            public Boolean apply(Boolean input) {
                return !input;
            }
        });
    }
}
