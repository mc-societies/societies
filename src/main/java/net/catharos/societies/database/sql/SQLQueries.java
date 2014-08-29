package net.catharos.societies.database.sql;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.catharos.lib.database.DSLProvider;
import net.catharos.lib.database.QueryKey;
import net.catharos.lib.database.QueryProvider;
import net.catharos.societies.database.layout.tables.records.MembersRecord;
import net.catharos.societies.database.layout.tables.records.SocietiesRecord;
import net.catharos.societies.database.layout.tables.records.SocietiesSettingsRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.types.UShort;

import static net.catharos.societies.database.layout.Tables.*;

/**
 * Represents a SocietiesQueries
 */
@Singleton
class SQLQueries extends QueryProvider {

    public static final QueryKey<Select<SocietiesRecord>> SELECT_SOCIETIES = QueryKey.create();

    public static final QueryKey<Select<Record1<byte[]>>> SELECT_SOCIETY_MEMBERS = QueryKey.create();

    public static final QueryKey<Select<SocietiesRecord>> SELECT_SOCIETY_BY_UUID = QueryKey.create();

    public static final QueryKey<Select<SocietiesRecord>> SELECT_SOCIETY_BY_NAME = QueryKey.create();

    public static final QueryKey<Insert<SocietiesRecord>> INSERT_SOCIETY = QueryKey.create();

    public static final QueryKey<Update<SocietiesRecord>> UPDATE_SOCIETY_NAME = QueryKey.create();

    public static final QueryKey<Update<SocietiesRecord>> UPDATE_SOCIETY_STATE = QueryKey.create();

    public static final QueryKey<Update<SocietiesRecord>> UPDATE_SOCIETY_LAST_ACTIVE = QueryKey.create();

    public static final QueryKey<Query> DROP_SOCIETY_BY_UUID = QueryKey.create();

    public static final QueryKey<Select<Record3<byte[], UShort, byte[]>>> SELECT_SOCIETY_SETTINGS = QueryKey.create();

    public static final QueryKey<Insert<SocietiesSettingsRecord>> INSERT_GROUP_SETTING = QueryKey.create();


    //================================================================================
    // Members
    //================================================================================

    public static final QueryKey<Select<MembersRecord>> SELECT_MEMBER_BY_UUID = QueryKey.create();

    public static final QueryKey<Select<Record1<byte[]>>> SELECT_MEMBER_RANKS = QueryKey.create();

    public static final QueryKey<Update<MembersRecord>> UPDATE_MEMBER_SOCIETY = QueryKey.create();

    public static final QueryKey<Update<MembersRecord>> UPDATE_MEMBER_STATE = QueryKey.create();

    public static final QueryKey<Insert<MembersRecord>> INSERT_MEMBER = QueryKey.create();

    public static final QueryKey<Query> DROP_MEMBER_BY_UUID = QueryKey.create();

    @Inject
    protected SQLQueries(DSLProvider provider) {
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

        builder(INSERT_GROUP_SETTING, new QueryBuilder<Insert<SocietiesSettingsRecord>>() {
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
                        select(MEMBERS_RANKS.RANK).from(MEMBERS)
                        .where(MEMBERS_RANKS.MEMBER.equal(DEFAULT_UUID));
            }
        });

        builder(INSERT_MEMBER, new QueryBuilder<Insert<MembersRecord>>() {
            @Override
            public Insert<MembersRecord> create(DSLContext context) {
                return context
                        .insertInto(MEMBERS)
                        .set(MEMBERS.UUID, DEFAULT_BYTE_ARRAY)
                        .set(MEMBERS.SOCIETY, DEFAULT_BYTE_ARRAY);
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

    }
}
