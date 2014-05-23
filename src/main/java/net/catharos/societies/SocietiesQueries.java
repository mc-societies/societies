package net.catharos.societies;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.catharos.lib.database.DSLProvider;
import net.catharos.lib.database.QueryKey;
import net.catharos.lib.database.QueryProvider;
import net.catharos.societies.database.layout.tables.records.MembersRecord;
import net.catharos.societies.database.layout.tables.records.SocietiesRecord;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Select;

import static net.catharos.societies.database.layout.Tables.*;

/**
 * Represents a SocietiesQueries
 */
@Singleton
public class SocietiesQueries extends QueryProvider {

    public static final QueryKey<Select<SocietiesRecord>> SELECT_SOCIETY = new QueryKey<Select<SocietiesRecord>>();

    public static final QueryKey<Select<MembersRecord>> SELECT_MEMBER = new QueryKey<Select<MembersRecord>>();

    public static final QueryKey<Select<Record1<byte[]>>> SELECT_MEMBER_RANKS = new QueryKey<Select<Record1<byte[]>>>();

    public static final byte[] DEFAULT_UUID = new byte[0];

    @Inject
    protected SocietiesQueries(DSLProvider provider) {
        super(provider);
    }

    @Override
    public void build() {

        builder(SELECT_SOCIETY, new QueryBuilder<Select<SocietiesRecord>>() {
            @Override
            public Select<SocietiesRecord> create(DSLContext context) {
                return context.
                        selectFrom(SOCIETIES)
                        .where(SOCIETIES.UUID.equal(DEFAULT_UUID));
            }
        });

        builder(SELECT_MEMBER, new QueryBuilder<Select<MembersRecord>>() {
            @Override
            public Select<MembersRecord> create(DSLContext context) {
                return context.
                        selectFrom(MEMBERS)
                        .where(MEMBERS.UUID.equal(DEFAULT_UUID));
            }
        });

        builder(SELECT_MEMBER_RANKS, new QueryBuilder<Select<Record1<byte[]>>>() {
            @Override
            public Select<Record1<byte[]>> create(DSLContext context) {
                return context.
                        select(MEMBERS_RANKS.RANK).from(MEMBERS)
                        .where(MEMBERS_RANKS.MEMBER.equal(DEFAULT_UUID));
            }
        });

//        builder(null, new QueryBuilder<Select<Record1<byte[]>>>() {
//            @Override
//            public Select<Record1<byte[]>> create(DSLContext context) {
//                return context.
//                        select
//                        selectFrom(SOCIETIES)
//                        .where(SOCIETIES.CREATED.lessThan(DSL.dateAdd(DSL.currentDate(), 5)));
//            }
//        });
    }
}
