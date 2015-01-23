package org.societies.lock.sql;

import com.google.inject.Inject;
import org.jooq.DSLContext;
import org.jooq.Insert;
import org.jooq.Query;
import org.jooq.Select;
import org.societies.database.DSLProvider;
import org.societies.database.QueryKey;
import org.societies.database.QueryProvider;
import org.societies.database.sql.layout.tables.records.SocietiesLocksRecord;

import static org.societies.database.sql.layout.Tables.SOCIETIES_LOCKS;

/**
 * Represents a Queries
 */
public class LockQueries extends QueryProvider {

    //================================================================================
    // Locks
    //================================================================================

    public static final QueryKey<Insert<SocietiesLocksRecord>> INSERT_LOCK = QueryKey.create();

    public static final QueryKey<Query> DROP_LOCK = QueryKey.create();

    public static final QueryKey<Select<SocietiesLocksRecord>> SELECT_LOCK = QueryKey.create();

    @Inject
    public LockQueries(DSLProvider provider) {
        super(provider);
    }

    @Override
    public void build() {
        //================================================================================
        // Locks
        //================================================================================


        builder(INSERT_LOCK, new QueryBuilder<Insert<SocietiesLocksRecord>>() {
            @Override
            public Insert<SocietiesLocksRecord> create(DSLContext context) {
                return context.insertInto(SOCIETIES_LOCKS).set(SOCIETIES_LOCKS.ID, DEFAULT_SHORT)
                        .onDuplicateKeyIgnore();
            }
        });

        builder(DROP_LOCK, new QueryBuilder<Query>() {
            @Override
            public Query create(DSLContext context) {
                return context.delete(SOCIETIES_LOCKS).where(SOCIETIES_LOCKS.ID.equal(DEFAULT_SHORT));
            }
        });

        builder(SELECT_LOCK, new QueryBuilder<Select<SocietiesLocksRecord>>() {
            @Override
            public Select<SocietiesLocksRecord> create(DSLContext context) {
                return context.selectFrom(SOCIETIES_LOCKS).where(SOCIETIES_LOCKS.ID.equal(DEFAULT_SHORT));
            }
        });

    }
}
