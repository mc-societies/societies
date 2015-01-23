package org.societies.sql;

import com.google.inject.Inject;
import org.jooq.*;
import org.jooq.types.UShort;
import org.societies.database.DSLProvider;
import org.societies.database.QueryKey;
import org.societies.database.QueryProvider;
import org.societies.database.sql.layout.tables.records.*;

import java.sql.Timestamp;

import static org.societies.database.sql.layout.Tables.*;

/**
 * Represents a Queries
 */
class Queries extends QueryProvider {

    //================================================================================
    // Groups
    //================================================================================

    public static final QueryKey<Insert<SocietiesRecord>> INSERT_SOCIETY = QueryKey.create();

    public static final QueryKey<Update<SocietiesRecord>> UPDATE_SOCIETY_NAME = QueryKey.create();
    public static final QueryKey<Update<SocietiesRecord>> UPDATE_SOCIETY_TAG = QueryKey.create();
    public static final QueryKey<Update<SocietiesRecord>> UPDATE_SOCIETY_CREATED = QueryKey.create();

    public static final QueryKey<Select<Record1<String>>> SELECT_SOCIETY_NAME = QueryKey.create();
    public static final QueryKey<Select<Record1<String>>> SELECT_SOCIETY_TAG = QueryKey.create();
    public static final QueryKey<Select<Record1<Timestamp>>> SELECT_SOCIETY_CREATED = QueryKey.create();


    public static final QueryKey<Select<SocietiesRecord>> SELECT_SOCIETIES = QueryKey.create();
    public static final QueryKey<Select<Record1<Integer>>> SELECT_SOCIETIES_AMOUNT = QueryKey.create();


    public static final QueryKey<Select<SocietiesRecord>> SELECT_SOCIETY_BY_UUID = QueryKey.create();
    public static final QueryKey<Select<SocietiesRecord>> SELECT_SOCIETY_BY_TAG = QueryKey.create();


    public static final QueryKey<Select<Record1<byte[]>>> SELECT_SOCIETY_MEMBERS = QueryKey.create();
    public static final QueryKey<Select<Record3<byte[], UShort, byte[]>>> SELECT_SOCIETY_SETTINGS = QueryKey.create();
    public static final QueryKey<Insert<SocietiesSettingsRecord>> INSERT_SOCIETY_SETTING = QueryKey.create();

    public static final QueryKey<Select<Record3<byte[], String, Short>>> SELECT_SOCIETY_RANKS = QueryKey.create();
    public static final QueryKey<Select<Record3<byte[], UShort, byte[]>>> SELECT_RANK_SETTINGS = QueryKey.create();
    public static final QueryKey<Insert> INSERT_SOCIETY_RANK = QueryKey.create();


    public static final QueryKey<Query> DROP_SOCIETY_BY_UUID = QueryKey.create();


    public static final QueryKey<Query> DROP_RANK_IN_SOCIETIES = QueryKey.create();


    // Members
    //================================================================================

    public static final QueryKey<Select<MembersRecord>> SELECT_MEMBER_BY_UUID = QueryKey.create();

    public static final QueryKey<Select<Record1<Timestamp>>> SELECT_MEMBER_CREATED = QueryKey.create();

    public static final QueryKey<Select<Record1<Timestamp>>> SELECT_MEMBER_LAST_ACTIVE = QueryKey.create();

    public static final QueryKey<Select<Record1<byte[]>>> SELECT_MEMBER_SOCIETY = QueryKey.create();

    public static final QueryKey<Select<MembersRecord>> SELECT_MEMBERS = QueryKey.create();

    public static final QueryKey<Select<Record3<byte[], UShort, byte[]>>> SELECT_MEMBER_SETTINGS = QueryKey.create();

    public static final QueryKey<Insert<MemberSettingsRecord>> INSERT_MEMBER_SETTING = QueryKey.create();


    public static final QueryKey<Insert<MembersRecord>> INSERT_MEMBER = QueryKey.create();

    public static final QueryKey<Update<MembersRecord>> UPDATE_MEMBER_SOCIETY = QueryKey.create();

    public static final QueryKey<Query> DROP_MEMBER_BY_UUID = QueryKey.create();

    public static final QueryKey<Update<MembersRecord>> UPDATE_MEMBER_LAST_ACTIVE = QueryKey.create();

    public static final QueryKey<Update<MembersRecord>> UPDATE_MEMBER_CREATED = QueryKey.create();

    public static final QueryKey<Select<Record1<byte[]>>> SELECT_MEMBER_RANKS = QueryKey.create();

    public static final QueryKey<Insert> INSERT_MEMBER_RANK = QueryKey.create();

    public static final QueryKey<Query> DROP_MEMBER_RANK = QueryKey.create();

    public static final QueryKey<Query> DROP_RANK_IN_MEMBERS = QueryKey.create();

    //================================================================================
    // Ranks
    //================================================================================

    public static final QueryKey<Insert<RanksRecord>> INSERT_RANK = QueryKey.create();
    public static final QueryKey<Query> DROP_RANK = QueryKey.create();


    @Inject
    public Queries(DSLProvider provider) {
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

        builder(SELECT_SOCIETIES_AMOUNT, new QueryBuilder<Select<Record1<Integer>>>() {
            @Override
            public Select<Record1<Integer>> create(DSLContext context) {
                return context.selectCount().from(SOCIETIES);
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

        builder(SELECT_SOCIETY_BY_TAG, new QueryBuilder<Select<SocietiesRecord>>() {
            @Override
            public Select<SocietiesRecord> create(DSLContext context) {
                return context.
                        selectFrom(SOCIETIES)
                        .where(SOCIETIES.CLEAN_TAG.like(DEFAULT_STRING));
            }
        });

        builder(SELECT_SOCIETY_RANKS, new QueryBuilder<Select<Record3<byte[], String, Short>>>() {
            @Override
            public Select<Record3<byte[], String, Short>> create(DSLContext context) {
                return context.select(RANKS.UUID, RANKS.NAME, RANKS.PRIORITY).from(RANKS)
                        .join(SOCIETIES_RANKS)
                        .on(SOCIETIES_RANKS.RANK.eq(RANKS.UUID))
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
                        .set(SOCIETIES.CLEAN_TAG, DEFAULT_STRING)
                        .set(SOCIETIES.CREATED, DEFAULT_TIMESTAMP).onDuplicateKeyIgnore();
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

        builder(UPDATE_SOCIETY_TAG, new QueryBuilder<Update<SocietiesRecord>>() {
            @Override
            public Update<SocietiesRecord> create(DSLContext context) {
                return context.update(SOCIETIES)
                        .set(SOCIETIES.TAG, DEFAULT_STRING)
                        .set(SOCIETIES.CLEAN_TAG, DEFAULT_STRING)
                        .where(SOCIETIES.UUID.equal(DEFAULT_BYTE_ARRAY));
            }
        });

        builder(UPDATE_SOCIETY_CREATED, new QueryBuilder<Update<SocietiesRecord>>() {
            @Override
            public Update<SocietiesRecord> create(DSLContext context) {
                return context.update(SOCIETIES)
                        .set(SOCIETIES.CREATED, DEFAULT_TIMESTAMP)
                        .where(SOCIETIES.UUID.equal(DEFAULT_BYTE_ARRAY));
            }
        });

        builder(SELECT_SOCIETY_TAG, new QueryBuilder<Select<Record1<String>>>() {
            @Override
            public Select<Record1<String>> create(DSLContext context) {
                return context.select(SOCIETIES.TAG).from(SOCIETIES).where(SOCIETIES.UUID.equal(DEFAULT_BYTE_ARRAY));
            }
        });

        builder(SELECT_SOCIETY_NAME, new QueryBuilder<Select<Record1<String>>>() {
            @Override
            public Select<Record1<String>> create(DSLContext context) {
                return context.select(SOCIETIES.NAME).from(SOCIETIES).where(SOCIETIES.UUID.equal(DEFAULT_BYTE_ARRAY));
            }
        });

        builder(SELECT_SOCIETY_CREATED, new QueryBuilder<Select<Record1<Timestamp>>>() {
            @Override
            public Select<Record1<Timestamp>> create(DSLContext context) {
                return context.select(SOCIETIES.CREATED).from(SOCIETIES)
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

        builder(SELECT_MEMBER_SETTINGS, new QueryBuilder<Select<Record3<byte[], UShort, byte[]>>>() {
            @Override
            public Select<Record3<byte[], UShort, byte[]>> create(DSLContext context) {
                return context
                        .select(MEMBER_SETTINGS.TARGET_UUID, MEMBER_SETTINGS.SETTING, MEMBER_SETTINGS.VALUE)
                        .from(MEMBER_SETTINGS)
                        .where(MEMBER_SETTINGS.SUBJECT_UUID.equal(DEFAULT_BYTE_ARRAY));
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

        builder(INSERT_MEMBER_SETTING, new QueryBuilder<Insert<MemberSettingsRecord>>() {
            @Override
            public Insert<MemberSettingsRecord> create(DSLContext context) {
                return context
                        .insertInto(MEMBER_SETTINGS)
                        .values(DEFAULT_BYTE_ARRAY, DEFAULT_BYTE_ARRAY, UShort.valueOf(0), DEFAULT_BYTE_ARRAY)
                        .onDuplicateKeyUpdate()
                        .set(MEMBER_SETTINGS.SUBJECT_UUID, DEFAULT_BYTE_ARRAY)
                        .set(MEMBER_SETTINGS.TARGET_UUID, DEFAULT_BYTE_ARRAY)
                        .set(MEMBER_SETTINGS.SETTING, UShort.valueOf(0))
                        .set(MEMBER_SETTINGS.VALUE, DEFAULT_BYTE_ARRAY);
            }
        });

        builder(INSERT_SOCIETY_RANK, new QueryBuilder<Insert>() {
            @Override
            public Insert create(DSLContext context) {
                return context.insertInto(SOCIETIES_RANKS)
                        .values(DEFAULT_BYTE_ARRAY, DEFAULT_BYTE_ARRAY);
            }
        });

        builder(DROP_RANK_IN_SOCIETIES, new QueryBuilder<Query>() {
            @Override
            public Query create(DSLContext context) {
                return context.delete(SOCIETIES_RANKS)
                        .where(SOCIETIES_RANKS.RANK.equal(DEFAULT_BYTE_ARRAY));
            }
        });

        //================================================================================
        // Rank
        //================================================================================

        builder(INSERT_RANK, new QueryBuilder<Insert<RanksRecord>>() {
            @Override
            public Insert<RanksRecord> create(DSLContext context) {
                return context.insertInto(RANKS)
                        .values(DEFAULT_BYTE_ARRAY, DEFAULT_STRING, DEFAULT_SHORT)
                        .onDuplicateKeyUpdate()
                        .set(RANKS.UUID, DEFAULT_BYTE_ARRAY)
                        .set(RANKS.NAME, DEFAULT_STRING)
                        .set(RANKS.PRIORITY, DEFAULT_SHORT);
            }
        });

        builder(DROP_RANK, new QueryBuilder<Query>() {
            @Override
            public Query create(DSLContext context) {
                return context.delete(RANKS).where(RANKS.UUID.equal(DEFAULT_BYTE_ARRAY));
            }
        });

        //================================================================================
        //  Members
        //================================================================================

        builder(SELECT_MEMBERS, new QueryBuilder<Select<MembersRecord>>() {
            @Override
            public Select<MembersRecord> create(DSLContext context) {
                return context.
                        selectFrom(MEMBERS);
            }
        });

        builder(SELECT_MEMBER_CREATED, new QueryBuilder<Select<Record1<Timestamp>>>() {
            @Override
            public Select<Record1<Timestamp>> create(DSLContext context) {
                return context.select(MEMBERS.CREATED).from(MEMBERS).where(MEMBERS.UUID.equal(DEFAULT_UUID));
            }
        });

        builder(SELECT_MEMBER_LAST_ACTIVE, new QueryBuilder<Select<Record1<Timestamp>>>() {
            @Override
            public Select<Record1<Timestamp>> create(DSLContext context) {
                return context.select(MEMBERS.LASTACTIVE).from(MEMBERS).where(MEMBERS.UUID.equal(DEFAULT_UUID));
            }
        });

        builder(SELECT_MEMBER_SOCIETY, new QueryBuilder<Select<Record1<byte[]>>>() {
            @Override
            public Select<Record1<byte[]>> create(DSLContext context) {
                return context.select(MEMBERS.SOCIETY).from(MEMBERS).where(MEMBERS.UUID.equal(DEFAULT_UUID));
            }
        });

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
                return context.delete(MEMBERS_RANKS)
                        .where(MEMBERS_RANKS.MEMBER.equal(DEFAULT_BYTE_ARRAY)
                                .and(MEMBERS_RANKS.RANK.equal(DEFAULT_BYTE_ARRAY)));
            }
        });

        builder(DROP_RANK_IN_MEMBERS, new QueryBuilder<Query>() {
            @Override
            public Query create(DSLContext context) {
                return context.delete(MEMBERS_RANKS).where(MEMBERS_RANKS.RANK.equal(DEFAULT_BYTE_ARRAY));
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

        builder(UPDATE_MEMBER_CREATED, new QueryBuilder<Update<MembersRecord>>() {
            @Override
            public Update<MembersRecord> create(DSLContext context) {
                return context.update(MEMBERS)
                        .set(MEMBERS.CREATED, DEFAULT_TIMESTAMP)
                        .where(MEMBERS.UUID.equal(DEFAULT_BYTE_ARRAY));
            }
        });
    }
}
