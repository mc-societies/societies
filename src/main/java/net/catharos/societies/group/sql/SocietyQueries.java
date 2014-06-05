package net.catharos.societies.group.sql;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.catharos.lib.database.DSLProvider;
import net.catharos.lib.database.QueryKey;
import net.catharos.lib.database.QueryProvider;
import net.catharos.societies.database.layout.tables.records.SocietiesRecord;
import org.jooq.DSLContext;
import org.jooq.Insert;
import org.jooq.Select;
import org.jooq.impl.DSL;

import static net.catharos.societies.database.layout.Tables.SOCIETIES;

/**
 * Represents a SocietiesQueries
 */
@Singleton
class SocietyQueries extends QueryProvider {

    public static final QueryKey<Select<SocietiesRecord>> SELECT_SOCIETIES = QueryKey.create();

    public static final QueryKey<Select<SocietiesRecord>> SELECT_SOCIETY_BY_UUID = QueryKey.create();

    public static final QueryKey<Select<SocietiesRecord>> SELECT_SOCIETY_BY_NAME = QueryKey.create();

    public static final QueryKey<Insert<SocietiesRecord>> INSERT_SOCIETY = QueryKey.create();

    @Inject
    protected SocietyQueries(DSLProvider provider) {
        super(provider);
    }

    @Override
    public void build() {

        builder(SELECT_SOCIETIES, new QueryBuilder<Select<SocietiesRecord>>() {
            @Override
            public Select<SocietiesRecord> create(DSLContext context) {
                return context.
                        selectFrom(SOCIETIES);
            }
        });

        builder(SELECT_SOCIETY_BY_UUID, new QueryBuilder<Select<SocietiesRecord>>() {
            @Override
            public Select<SocietiesRecord> create(DSLContext context) {
                return context.
                        selectFrom(SOCIETIES)
                        .where(SOCIETIES.UUID.equal(DEFAULT_UUID));
            }
        });

        builder(SELECT_SOCIETY_BY_NAME, new QueryBuilder<Select<SocietiesRecord>>() {
            @Override
            public Select<SocietiesRecord> create(DSLContext context) {
                return context.
                        selectFrom(SOCIETIES)
                        .where(SOCIETIES.NAME.like(""));
            }
        });

        builder(INSERT_SOCIETY, new QueryBuilder<Insert<SocietiesRecord>>() {
            @Override
            public Insert<SocietiesRecord> create(DSLContext context) {
                return context
                        .insertInto(SOCIETIES)
                        .set(SOCIETIES.UUID, DEFAULT_BYTE_ARRAY)
                        .set(SOCIETIES.NAME, DEFAULT_STRING)
                        .set(SOCIETIES.TAG, DEFAULT_STRING)
                        .set(SOCIETIES.LASTACTIVE, DSL.currentTimestamp());
            }
        });

    }
}
