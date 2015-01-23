package org.societies.database.sql;

import com.google.inject.Inject;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.societies.database.DSLProvider;
import org.societies.database.QueryKey;
import org.societies.database.QueryProvider;

import java.sql.Timestamp;

import static org.societies.database.sql.layout.Tables.*;

/**
 * Represents a Queries
 */
class CleanupQueries extends QueryProvider {

    public static final QueryKey<Query> DROP_ORPHAN_SOCIETIES = QueryKey.create();
    public static final QueryKey<Query> DROP_INACTIVE_MEMBERS = QueryKey.create();
    public static final QueryKey<Query> DROP_RANK_ORPHANS = QueryKey.create();

    @Inject
    public CleanupQueries(DSLProvider provider) {
        super(provider);
    }

    @Override
    public void build() {
        builder(DROP_ORPHAN_SOCIETIES, new QueryBuilder<Query>() {
            @Override
            public Query create(DSLContext context) {
                return context.delete(SOCIETIES)
                        .where(SOCIETIES.UUID
                                .notIn(context.select(MEMBERS.SOCIETY).from(MEMBERS)));
            }
        });

        builder(DROP_RANK_ORPHANS, new QueryBuilder<Query>() {
            @Override
            public Query create(DSLContext context) {
                return context.delete(RANKS)
                        .where(RANKS.UUID
                                .notIn(context.select(SOCIETIES_RANKS.RANK).from(SOCIETIES_RANKS))
                                .and(RANKS.UUID
                                        .notIn(context.select(MEMBERS_RANKS.RANK).from(MEMBERS_RANKS))));
            }
        });

        builder(DROP_INACTIVE_MEMBERS, new QueryBuilder<Query>() {
            @Override
            public Query create(DSLContext context) {
                return context.delete(MEMBERS)
                        .where(MEMBERS.CREATED.le(new Timestamp(System.currentTimeMillis())));
            }
        });
    }
}
