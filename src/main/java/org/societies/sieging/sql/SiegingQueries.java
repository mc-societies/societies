package org.societies.sieging.sql;

import com.google.inject.Inject;
import org.joda.time.DateTime;
import org.jooq.*;
import org.societies.database.DSLProvider;
import org.societies.database.QueryKey;
import org.societies.database.QueryProvider;
import org.societies.database.sql.layout.tables.records.CitiesRecord;
import org.societies.database.sql.layout.tables.records.LandsRecord;
import org.societies.database.sql.layout.tables.records.SiegesRecord;

import java.util.UUID;

import static org.jooq.impl.DSL.param;
import static org.societies.database.sql.layout.Tables.*;

/**
 * Represents a Queries
 */
class SiegingQueries extends QueryProvider {

    //================================================================================
    // Sieging
    //================================================================================

    public static final QueryKey<Insert> INSERT_LAND = QueryKey.create();

    public static final QueryKey<Delete<LandsRecord>> DROP_LAND = QueryKey.create();

    public static final QueryKey<Select<Record1<UUID>>> SELECT_LANDS_BY_CITY = QueryKey.create();


    public static final QueryKey<Insert> INSERT_CITY = QueryKey.create();

    public static final QueryKey<Delete<CitiesRecord>> DROP_CITY = QueryKey.create();

    public static final QueryKey<Select<CitiesRecord>> SELECT_CITIES_BY_SOCIETY = QueryKey.create();


    public static final QueryKey<Insert> INSERT_SIEGE = QueryKey.create();

    public static final QueryKey<Delete<SiegesRecord>> DROP_SIEGE_BY_SIEGE = QueryKey.create();

    public static final QueryKey<Select<SiegesRecord>> SELECT_SIEGE_BY_SIEGE = QueryKey.create();

    public static final QueryKey<Select<SiegesRecord>> SELECT_SIEGES_BY_CITY = QueryKey.create();

    public static final QueryKey<Select<SiegesRecord>> SELECT_SIEGES_BY_BESIEGER = QueryKey.create();

    public static final QueryKey<Select<Record8<UUID, UUID, UUID, Short, Short, Short, UUID, DateTime>>> SELECT_SIEGES_BY_SIEGED = QueryKey
            .create();

    @Inject
    public SiegingQueries(DSLProvider provider) {
        super(provider);
    }

    @Override
    public void build() {
        //================================================================================
        // Sieging
        //================================================================================

        builder(INSERT_LAND, new QueryBuilder<Insert>() {
            @Override
            public Insert create(DSLContext context) {
                return context.insertInto(LANDS)
                        .set(LANDS.UUID, uuid_param())
                        .set(LANDS.ORIGIN, uuid_param("origin"));

            }
        });

        builder(DROP_LAND, new QueryBuilder<Delete<LandsRecord>>() {
            @Override
            public Delete<LandsRecord> create(DSLContext context) {
                return context.delete(LANDS)
                        .where(LANDS.UUID.equal(id_param()));
            }
        });

        builder(SELECT_LANDS_BY_CITY, new QueryBuilder<Select<Record1<UUID>>>() {
            @Override
            public Select<Record1<UUID>> create(DSLContext context) {
                return context.select(LANDS.UUID).from(LANDS).where(LANDS.ORIGIN.equal(uuid_param("origin")));
            }
        });


        builder(INSERT_CITY, new QueryBuilder<Insert>() {
            @Override
            public Insert create(DSLContext context) {
                return context.insertInto(CITIES)
                        .set(CITIES.UUID, id_param())
                        .set(CITIES.SOCIETY, uuid_param("society"))

                        .set(CITIES.X, DEFAULT_SHORT)
                        .set(CITIES.Y, DEFAULT_SHORT)
                        .set(CITIES.Z, DEFAULT_SHORT);
            }
        });

        builder(DROP_CITY, new QueryBuilder<Delete<CitiesRecord>>() {
            @Override
            public Delete<CitiesRecord> create(DSLContext context) {
                return context.delete(CITIES)
                        .where(CITIES.UUID.equal(id_param()));
            }
        });

        builder(SELECT_SIEGE_BY_SIEGE, new QueryBuilder<Select<SiegesRecord>>() {
            @Override
            public Select<SiegesRecord> create(DSLContext context) {
                return context.selectFrom(SIEGES).where(SIEGES.UUID.equal(uuid_param()));
            }
        });

        builder(SELECT_CITIES_BY_SOCIETY, new QueryBuilder<Select<CitiesRecord>>() {
            @Override
            public Select<CitiesRecord> create(DSLContext context) {
                return context.selectFrom(CITIES).where(CITIES.SOCIETY.equal(uuid_param("society")));
            }
        });


        builder(INSERT_SIEGE, new QueryBuilder<Insert>() {
            @Override
            public Insert create(DSLContext context) {
                return context.insertInto(SIEGES)
                        .set(SIEGES.UUID, id_param())
                        .set(SIEGES.SOCIETY, uuid_param("society"))
                        .set(SIEGES.CITY, uuid_param("city"))

                        .set(SIEGES.X, param("x", short.class))
                        .set(SIEGES.Y, param("y", short.class))
                        .set(SIEGES.Z, param("z", short.class))

                        .set(SIEGES.WAGER, uuid_param("wager"));
            }
        });

        builder(DROP_SIEGE_BY_SIEGE, new QueryBuilder<Delete<SiegesRecord>>() {
            @Override
            public Delete<SiegesRecord> create(DSLContext context) {
                return context.delete(SIEGES)
                        .where(SIEGES.UUID.equal(uuid_param()));
            }
        });

        builder(SELECT_SIEGES_BY_CITY, new QueryBuilder<Select<SiegesRecord>>() {
            @Override
            public Select<SiegesRecord> create(DSLContext context) {
                return context.selectFrom(SIEGES).where(SIEGES.CITY
                        .equal(uuid_param("city")));
            }
        });

        builder(SELECT_SIEGES_BY_BESIEGER, new QueryBuilder<Select<SiegesRecord>>() {
            @Override
            public Select<SiegesRecord> create(DSLContext context) {
                return context.selectFrom(SIEGES).where(SIEGES.SOCIETY
                        .equal(uuid_param("besieger")));
            }
        });

        builder(SELECT_SIEGES_BY_SIEGED, new QueryBuilder<Select<Record8<UUID, UUID, UUID, Short, Short, Short, UUID, DateTime>>>() {
            @Override
            public Select<Record8<UUID, UUID, UUID, Short, Short, Short, UUID, DateTime>> create(DSLContext context) {
                return context.select(SIEGES.UUID, SIEGES.SOCIETY, SIEGES.CITY, SIEGES.X, SIEGES.Y, SIEGES.Z, SIEGES.WAGER, SIEGES.CREATED)
                        .from(SIEGES)
                        .join(CITIES)
                        .on(SIEGES.SOCIETY.equal(CITIES.SOCIETY))
                        .and(SIEGES.SOCIETY.equal(uuid_param("sieged")));
            }
        });
    }
}
