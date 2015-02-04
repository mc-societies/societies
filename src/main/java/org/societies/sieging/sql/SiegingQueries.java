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

import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.power;
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

    public static final QueryKey<Select<CitiesRecord>> SELECT_NEAREST = QueryKey.create();

    public static final QueryKey<Select<CitiesRecord>> SELECT_CITY_BY_UUID = QueryKey.create();
    public static final QueryKey<Select<CitiesRecord>> SELECT_CITY_BY_NAME = QueryKey.create();


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
                        .set(LANDS.UUID, id_param())
                        .set(LANDS.ORIGIN, id_param());

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
                return context.select(LANDS.UUID).from(LANDS).where(LANDS.ORIGIN.equal(id_param()));
            }
        });


        builder(INSERT_CITY, new QueryBuilder<Insert>() {
            @Override
            public Insert create(DSLContext context) {
                return context.insertInto(CITIES)
                        .set(CITIES.UUID, id_param())
                        .set(CITIES.SOCIETY, id_param())
                        .set(CITIES.NAME, DEFAULT_STRING)
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
                return context.selectFrom(CITIES).where(CITIES.SOCIETY.equal(id_param()));
            }
        });


        builder(INSERT_SIEGE, new QueryBuilder<Insert>() {
            @Override
            public Insert create(DSLContext context) {
                return context.insertInto(SIEGES)
                        .set(SIEGES.UUID, id_param())
                        .set(SIEGES.SOCIETY, id_param())
                        .set(SIEGES.CITY, id_param())

                        .set(SIEGES.X, DEFAULT_SHORT)
                        .set(SIEGES.Y, DEFAULT_SHORT)
                        .set(SIEGES.Z, DEFAULT_SHORT)

                        .set(SIEGES.WAGER, id_param());
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
                        .equal(id_param()));
            }
        });

        builder(SELECT_SIEGES_BY_BESIEGER, new QueryBuilder<Select<SiegesRecord>>() {
            @Override
            public Select<SiegesRecord> create(DSLContext context) {
                return context.selectFrom(SIEGES).where(SIEGES.SOCIETY
                        .equal(id_param()));
            }
        });

        builder(SELECT_SIEGES_BY_SIEGED, new QueryBuilder<Select<Record8<UUID, UUID, UUID, Short, Short, Short, UUID, DateTime>>>() {
            @Override
            public Select<Record8<UUID, UUID, UUID, Short, Short, Short, UUID, DateTime>> create(DSLContext context) {
                return context.select(SIEGES.UUID, SIEGES.SOCIETY, SIEGES.CITY, SIEGES.X, SIEGES.Y, SIEGES.Z, SIEGES.WAGER, SIEGES.CREATED)
                        .from(SIEGES)
                        .join(CITIES)
                        .on(SIEGES.SOCIETY.equal(CITIES.SOCIETY))
                        .and(SIEGES.SOCIETY.equal(id_param()));
            }
        });


        builder(SELECT_NEAREST, new QueryBuilder<Select<CitiesRecord>>() {
            @Override
            public Select<CitiesRecord> create(DSLContext context) {
                return context.selectFrom(CITIES).where(
                        power(CITIES.X.minus(DEFAULT_DOUBLE), 2)
                                .add(power(CITIES.Y.minus(DEFAULT_DOUBLE), 2))
                                .add(power(CITIES.Z.minus(DEFAULT_DOUBLE), 2))
                                .cast(Integer.class)
                                .lessOrEqual(context.select(count().mul(5)).from(LANDS)
                                        .where(LANDS.ORIGIN.equal(CITIES.UUID)))
                );
            }
        });

        builder(SELECT_CITY_BY_UUID, new QueryBuilder<Select<CitiesRecord>>() {
            @Override
            public Select<CitiesRecord> create(DSLContext context) {
                return context.selectFrom(CITIES).where(CITIES.UUID.equal(uuid_param()));
            }
        });

        builder(SELECT_CITY_BY_NAME, new QueryBuilder<Select<CitiesRecord>>() {
            @Override
            public Select<CitiesRecord> create(DSLContext context) {
                return context.selectFrom(CITIES).where(CITIES.NAME.equal(DEFAULT_STRING));
            }
        });

    }
}
