package org.societies.lock.sql;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jooq.Query;
import org.jooq.Select;
import org.societies.api.lock.Locker;

/**
 * Represents a SQLLocker
 */
@Singleton
class SQLLocker implements Locker {
    private final LockQueries queries;

//todo    private final TIntObjectHashMap<ReentrantLock> locks = new TIntObjectHashMap<ReentrantLock>();

    @Inject
    public SQLLocker(LockQueries queries) {
        this.queries = queries;
    }

    @Override
    public boolean lock(final int id) {
        Query query = queries.getQuery(LockQueries.INSERT_LOCK);

        query.bind(1, id);

        return query.execute() > 0;
    }

    @Override
    public boolean unlock(final int id) {
        Query query = queries.getQuery(LockQueries.DROP_LOCK);

        query.bind(1, id);

        return query.execute() > 0;
    }

    @Override
    public boolean isLocked(final int id) {
        Select query = queries.getQuery(LockQueries.SELECT_LOCK);

        query.bind(1, id);

        return query.fetchCount() > 0;
    }

    @Override
    public boolean isFree(int id) {
        return !isLocked(id);
    }
}
