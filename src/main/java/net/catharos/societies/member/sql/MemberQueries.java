package net.catharos.societies.member.sql;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.catharos.lib.database.DSLProvider;
import net.catharos.lib.database.QueryKey;
import net.catharos.lib.database.QueryProvider;
import net.catharos.societies.database.layout.tables.records.MembersRecord;
import org.jooq.*;

import static net.catharos.societies.database.layout.Tables.MEMBERS;
import static net.catharos.societies.database.layout.Tables.MEMBERS_RANKS;

/**
 * Represents a SocietiesQueries
 */
@Singleton
class MemberQueries extends QueryProvider {

    public static final QueryKey<Select<MembersRecord>> SELECT_MEMBER_BY_UUID = QueryKey.create();

    public static final QueryKey<Select<Record1<byte[]>>> SELECT_MEMBER_RANKS = QueryKey.create();

    public static final QueryKey<Update<MembersRecord>> UPDATE_MEMBER_SOCIETY = QueryKey.create();

    public static final QueryKey<Insert<MembersRecord>> INSERT_MEMBER = QueryKey.create();

    public static final QueryKey<Query> DROP_MEMBER_BY_UUID = QueryKey.create();

    @Inject
    protected MemberQueries(DSLProvider provider) {
        super(provider);
    }

    @Override
    public void build() {

        builder(SELECT_MEMBER_BY_UUID, new QueryBuilder<Select<MembersRecord>>() {
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

        builder(INSERT_MEMBER, new QueryBuilder<Insert<MembersRecord>>() {
            @Override
            public Insert<MembersRecord> create(DSLContext context) {
                return context
                        .insertInto(MEMBERS)
                        .set(MEMBERS.UUID, DEFAULT_BYTE_ARRAY);
            }
        });

        builder(DROP_MEMBER_BY_UUID, new QueryBuilder<Query>() {
            @Override
            public Query create(DSLContext context) {
                return context
                        .delete(MEMBERS).where(MEMBERS.UUID.equal(DEFAULT_BYTE_ARRAY));
            }
        });

        builder(UPDATE_MEMBER_SOCIETY, new QueryBuilder<Update<MembersRecord>>() {
            @Override
            public Update<MembersRecord> create(DSLContext context) {
                return context.update(MEMBERS)
                        .set(MEMBERS.SOCIETY, DEFAULT_BYTE_ARRAY)
                        .where(MEMBERS.UUID.equal(DEFAULT_BYTE_ARRAY));
            }
        });
    }
}
