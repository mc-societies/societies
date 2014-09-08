package net.catharos.societies.database.sql;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.catharos.lib.database.DSLProvider;
import net.catharos.lib.database.QueryKey;
import net.catharos.lib.database.QueryProvider;
import net.catharos.societies.database.layout.tables.records.MembersRecord;
import net.catharos.societies.database.layout.tables.records.RanksRecord;
import net.catharos.societies.database.layout.tables.records.SocietiesRecord;
import net.catharos.societies.database.layout.tables.records.SocietiesSettingsRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.types.UShort;

import java.sql.Timestamp;

import static net.catharos.societies.database.layout.Tables.*;

/**
 * Represents a SocietiesQueries
 */
@Singleton
class SQLQueries extends QueryProvider {

    //================================================================================
    // Groups
    //================================================================================

    public static final QueryKey<Select<SocietiesRecord>> SELECT_SOCIETIES = QueryKey.create();


    public static final QueryKey<Select<SocietiesRecord>> SELECT_SOCIETY_BY_UUID = QueryKey.create();

    public static final QueryKey<Select<SocietiesRecord>> SELECT_SOCIETY_BY_NAME = QueryKey.create();


    public static final QueryKey<Insert<SocietiesRecord>> INSERT_SOCIETY = QueryKey.create();

    public static final QueryKey<Update<SocietiesRecord>> UPDATE_SOCIETY_NAME = QueryKey.create();

    public static final QueryKey<Update<SocietiesRecord>> UPDATE_SOCIETY_STATE = QueryKey.create();

    public static final QueryKey<Update<SocietiesRecord>> UPDATE_SOCIETY_LAST_ACTIVE = QueryKey.create();

    public static final QueryKey<Query> DROP_SOCIETY_BY_UUID = QueryKey.create();


    public static final QueryKey<Select<Record1<byte[]>>> SELECT_SOCIETY_MEMBERS = QueryKey.create();


    public static final QueryKey<Select<Record3<byte[], UShort, byte[]>>> SELECT_SOCIETY_SETTINGS = QueryKey.create();

    public static final QueryKey<Insert<SocietiesSettingsRecord>> INSERT_SOCIETY_SETTING = QueryKey.create();


    public static final QueryKey<Select<Record2<byte[], String>>> SELECT_GROUP_RANKS = QueryKey.create();

    public static final QueryKey<Select<Record3<byte[], UShort, byte[]>>> SELECT_RANK_SETTINGS = QueryKey.create();

    public static final QueryKey<Insert> INSERT_SOCIETY_RANK = QueryKey.create();
    public static final QueryKey<Query> DROP_SOCIETY_RANK = QueryKey.create();

    public static final QueryKey<Query> DROP_INACTIVE_SOCIETIES = QueryKey.create();


    //================================================================================
    // Ranks
    //================================================================================


    public static final QueryKey<Insert<RanksRecord>> INSERT_RANK = QueryKey.create();

    public static final QueryKey<Query> DROP_RANK = QueryKey.create();

    public static final QueryKey<Query> DROP_RANK_ORPHANS = QueryKey.create();


    //================================================================================
    // Members
    //================================================================================

    public static final QueryKey<Select<MembersRecord>> SELECT_MEMBER_BY_UUID = QueryKey.create();


    public static final QueryKey<Insert<MembersRecord>> INSERT_MEMBER = QueryKey.create();

    public static final QueryKey<Update<MembersRecord>> UPDATE_MEMBER_SOCIETY = QueryKey.create();

    public static final QueryKey<Update<MembersRecord>> UPDATE_MEMBER_STATE = QueryKey.create();

    public static final QueryKey<Query> DROP_MEMBER_BY_UUID = QueryKey.create();

    public static final QueryKey<Update<MembersRecord>> UPDATE_MEMBER_LAST_ACTIVE = QueryKey.create();


    public static final QueryKey<Select<Record1<byte[]>>> SELECT_MEMBER_RANKS = QueryKey.create();

    public static final QueryKey<Insert> INSERT_MEMBER_RANK = QueryKey.create();

    public static final QueryKey<Query> DROP_MEMBER_RANK = QueryKey.create();

    public static final QueryKey<Query> DROP_INACTIVE_MEMBERS = QueryKey.create();


    @Inject
    protected SQLQueries(DSLProvider provider) {
        super(provider);
    }

    @Override
    public void build() {

        //================================================================================
        // Groups
        //================================================================================

        builder(SELECT_SOCIETIES, new QueryBuilder<Select<SocietiesRecord>>() {
            @Override
            public Select<SocietiesRecord> create(DSLContext context) {
                return context.
                        selectFrom(SOCIETIES);
            }
        });

        builder(SELECT_SOCIETY_MEMBERS, new QueryBuilder<Select<Record1<byte[]>>>() {
            @Override
            public Select<Record1<byte[]>> create(DSLContext context) {
                return context.select(MEMBERS.UUID).
                        from(MEMBERS).
                        where(MEMBERS.SOCIETY.equal(DEFAULT_BYTE_ARRAY));
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
                        .where(SOCIETIES.NAME.like(DEFAULT_STRING));
            }
        });

        builder(SELECT_GROUP_RANKS, new QueryBuilder<Select<Record2<byte[], String>>>() {
            @Override
            public Select<Record2<byte[], String>> create(DSLContext context) {
                return context.select(RANKS.UUID, RANKS.NAME).from(RANKS).leftOuterJoin(SOCIETIES_RANKS)
                        .on(SOCIETIES_RANKS.SOCIETY.eq(RANKS.UUID))
                        .and(SOCIETIES_RANKS.SOCIETY.eq(DEFAULT_BYTE_ARRAY));
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

        builder(DROP_SOCIETY_BY_UUID, new QueryBuilder<Query>() {
            @Override
            public Query create(DSLContext context) {
                return context
                        .delete(SOCIETIES).where(SOCIETIES.UUID.equal(DEFAULT_BYTE_ARRAY));
            }
        });

        builder(UPDATE_SOCIETY_NAME, new QueryBuilder<Update<SocietiesRecord>>() {
            @Override
            public Update<SocietiesRecord> create(DSLContext context) {
                return context.update(SOCIETIES)
                        .set(SOCIETIES.NAME, DEFAULT_STRING)
                        .where(SOCIETIES.UUID.equal(DEFAULT_BYTE_ARRAY));
            }
        });

        builder(UPDATE_SOCIETY_STATE, new QueryBuilder<Update<SocietiesRecord>>() {
            @Override
            public Update<SocietiesRecord> create(DSLContext context) {
                return context.update(SOCIETIES)
                        .set(SOCIETIES.STATE, Short.MAX_VALUE)
                        .where(SOCIETIES.UUID.equal(DEFAULT_BYTE_ARRAY));
            }
        });

        builder(UPDATE_SOCIETY_LAST_ACTIVE, new QueryBuilder<Update<SocietiesRecord>>() {
            @Override
            public Update<SocietiesRecord> create(DSLContext context) {
                return context.update(SOCIETIES)
                        .set(SOCIETIES.LASTACTIVE, DEFAULT_TIMESTAMP)
                        .where(SOCIETIES.UUID.equal(DEFAULT_BYTE_ARRAY));
            }
        });

        builder(SELECT_SOCIETY_SETTINGS, new QueryBuilder<Select<Record3<byte[], UShort, byte[]>>>() {
            @Override
            public Select<Record3<byte[], UShort, byte[]>> create(DSLContext context) {
                return context
                        .select(SOCIETIES_SETTINGS.TARGET_UUID, SOCIETIES_SETTINGS.SETTING, SOCIETIES_SETTINGS.VALUE)
                        .from(SOCIETIES_SETTINGS)
                        .where(SOCIETIES_SETTINGS.SUBJECT_UUID.equal(DEFAULT_BYTE_ARRAY));
            }
        });

        builder(SELECT_RANK_SETTINGS, new QueryBuilder<Select<Record3<byte[], UShort, byte[]>>>() {
            @Override
            public Select<Record3<byte[], UShort, byte[]>> create(DSLContext context) {
                return context
                        .select(RANKS_SETTINGS.TARGET_UUID, RANKS_SETTINGS.SETTING, RANKS_SETTINGS.VALUE)
                        .from(RANKS_SETTINGS)
                        .where(RANKS_SETTINGS.SUBJECT_UUID.equal(DEFAULT_BYTE_ARRAY));
            }
        });

        builder(INSERT_SOCIETY_SETTING, new QueryBuilder<Insert<SocietiesSettingsRecord>>() {
            @Override
            public Insert<SocietiesSettingsRecord> create(DSLContext context) {
                return context
                        .insertInto(SOCIETIES_SETTINGS)
                        .values(DEFAULT_BYTE_ARRAY, DEFAULT_BYTE_ARRAY, UShort.valueOf(0), DEFAULT_BYTE_ARRAY)
                        .onDuplicateKeyUpdate()
                        .set(SOCIETIES_SETTINGS.SUBJECT_UUID, DEFAULT_BYTE_ARRAY)
                        .set(SOCIETIES_SETTINGS.TARGET_UUID, DEFAULT_BYTE_ARRAY)
                        .set(SOCIETIES_SETTINGS.SETTING, UShort.valueOf(0))
                        .set(SOCIETIES_SETTINGS.VALUE, DEFAULT_BYTE_ARRAY);
            }
        });

        builder(INSERT_SOCIETY_RANK, new QueryBuilder<Insert>() {
            @Override
            public Insert create(DSLContext context) {
                return context.insertInto(SOCIETIES_RANKS)
                        .values(DEFAULT_BYTE_ARRAY, DEFAULT_BYTE_ARRAY);
            }
        });

        builder(DROP_SOCIETY_RANK, new QueryBuilder<Query>() {
            @Override
            public Query create(DSLContext context) {
                return context.delete(SOCIETIES_RANKS).where(SOCIETIES_RANKS.RANK.equal(DEFAULT_BYTE_ARRAY));
            }
        });

        builder(DROP_INACTIVE_SOCIETIES, new QueryBuilder<Query>() {
            @Override
            public Query create(DSLContext context) {
                return context.delete(SOCIETIES)
                        .where(SOCIETIES.LASTACTIVE.le(new Timestamp(System.currentTimeMillis())));
            }
        });

        //================================================================================
        // Rank
        //================================================================================

        builder(INSERT_RANK, new QueryBuilder<Insert<RanksRecord>>() {
            @Override
            public Insert<RanksRecord> create(DSLContext context) {
                return context.insertInto(RANKS)
                        .values(DEFAULT_BYTE_ARRAY, DEFAULT_STRING);
            }
        });

        builder(DROP_RANK, new QueryBuilder<Query>() {
            @Override
            public Query create(DSLContext context) {
                return context.delete(RANKS).where(RANKS.UUID.equal(DEFAULT_BYTE_ARRAY));
            }
        });

        builder(DROP_RANK_ORPHANS, new QueryBuilder<Query>() {
            @Override
            public Query create(DSLContext context) {
                return context.delete(RANKS)
                        .where(RANKS.UUID.notIn(context.select(SOCIETIES_RANKS.RANK).from(SOCIETIES_RANKS))
                                .and(RANKS.UUID.notIn(context.select(MEMBERS_RANKS.RANK).from(MEMBERS_RANKS))));
            }
        });

        //================================================================================
        //  Members
        //================================================================================

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
                        select(MEMBERS_RANKS.RANK).from(MEMBERS_RANKS)
                        .where(MEMBERS_RANKS.MEMBER.equal(DEFAULT_UUID));
            }
        });

        builder(INSERT_MEMBER, new QueryBuilder<Insert<MembersRecord>>() {
            @Override
            public Insert<MembersRecord> create(DSLContext context) {
                return context
                        .insertInto(MEMBERS)
                        .set(MEMBERS.UUID, DEFAULT_BYTE_ARRAY)
                        .set(MEMBERS.SOCIETY, DEFAULT_BYTE_ARRAY)
                        .set(MEMBERS.LASTACTIVE, DSL.currentTimestamp());
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

        builder(UPDATE_MEMBER_STATE, new QueryBuilder<Update<MembersRecord>>() {
            @Override
            public Update<MembersRecord> create(DSLContext context) {
                return context.update(MEMBERS)
                        .set(MEMBERS.STATE, Short.MAX_VALUE)
                        .where(MEMBERS.UUID.equal(DEFAULT_BYTE_ARRAY));
            }
        });

        builder(INSERT_MEMBER_RANK, new QueryBuilder<Insert>() {
            @Override
            public Insert create(DSLContext context) {
                return context.insertInto(MEMBERS_RANKS)
                        .values(DEFAULT_BYTE_ARRAY, DEFAULT_BYTE_ARRAY);
            }
        });

        builder(DROP_MEMBER_RANK, new QueryBuilder<Query>() {
            @Override
            public Query create(DSLContext context) {
                return context.delete(MEMBERS_RANKS).where(MEMBERS_RANKS.RANK.equal(DEFAULT_BYTE_ARRAY));
            }
        });

        builder(DROP_INACTIVE_MEMBERS, new QueryBuilder<Query>() {
            @Override
            public Query create(DSLContext context) {
                return context.delete(MEMBERS)
                        .where(MEMBERS.CREATED.le(new Timestamp(System.currentTimeMillis())));
            }
        });

        builder(UPDATE_MEMBER_LAST_ACTIVE, new QueryBuilder<Update<MembersRecord>>() {
            @Override
            public Update<MembersRecord> create(DSLContext context) {
                return context.update(MEMBERS)
                        .set(MEMBERS.LASTACTIVE, DEFAULT_TIMESTAMP)
                        .where(MEMBERS.UUID.equal(DEFAULT_BYTE_ARRAY));
            }
        });

    }
}
