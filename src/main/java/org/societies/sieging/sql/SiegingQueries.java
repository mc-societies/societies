package org.societies.sieging.sql;

import com.google.inject.Inject;
import org.jooq.*;
import org.societies.database.DSLProvider;
import org.societies.database.QueryKey;
import org.societies.database.QueryProvider;
import org.societies.database.sql.layout.tables.records.CitiesRecord;
import org.societies.database.sql.layout.tables.records.LandsRecord;
import org.societies.database.sql.layout.tables.records.SiegesRecord;

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

    public static final QueryKey<Select<Record1<byte[]>>> SELECT_LANDS_BY_CITY = QueryKey.create();


    public static final QueryKey<Insert> INSERT_CITY = QueryKey.create();

    public static final QueryKey<Delete<CitiesRecord>> DROP_CITY = QueryKey.create();

    public static final QueryKey<Select<CitiesRecord>> SELECT_CITIES_BY_SOCIETY = QueryKey.create();


    public static final QueryKey<Insert> INSERT_SIEGE = QueryKey.create();

    public static final QueryKey<Delete<SiegesRecord>> DROP_SIEGE = QueryKey.create();

    public static final QueryKey<Select<SiegesRecord>> SELECT_SIEGES_BY_CITY = QueryKey.create();

    public static final QueryKey<Select<SiegesRecord>> SELECT_SIEGES_BY_SOCIETY = QueryKey.create();

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
                        .set(LANDS.UUID, DEFAULT_BYTE_ARRAY)
                        .set(LANDS.ORIGIN, DEFAULT_BYTE_ARRAY);

            }
        });

        builder(DROP_LAND, new QueryBuilder<Delete<LandsRecord>>() {
            @Override
            public Delete<LandsRecord> create(DSLContext context) {
                return context.delete(LANDS)
                        .where(LANDS.UUID.equal(DEFAULT_BYTE_ARRAY));
            }
        });

        builder(SELECT_LANDS_BY_CITY, new QueryBuilder<Select<Record1<byte[]>>>() {
            @Override
            public Select<Record1<byte[]>> create(DSLContext context) {
                return context.select(LANDS.UUID).from(LANDS).where(LANDS.ORIGIN.equal(DEFAULT_BYTE_ARRAY));
            }
        });


        builder(INSERT_CITY, new QueryBuilder<Insert>() {
            @Override
            public Insert create(DSLContext context) {
                return context.insertInto(CITIES)
                        .set(CITIES.UUID, DEFAULT_BYTE_ARRAY)
                        .set(CITIES.SOCIETY, DEFAULT_BYTE_ARRAY)

                        .set(CITIES.X, DEFAULT_SHORT)
                        .set(CITIES.Y, DEFAULT_SHORT)
                        .set(CITIES.Z, DEFAULT_SHORT);
            }
        });

        builder(DROP_CITY, new QueryBuilder<Delete<CitiesRecord>>() {
            @Override
            public Delete<CitiesRecord> create(DSLContext context) {
                return context.delete(CITIES)
                        .where(CITIES.UUID.equal(DEFAULT_BYTE_ARRAY));
            }
        });

        builder(SELECT_CITIES_BY_SOCIETY, new QueryBuilder<Select<CitiesRecord>>() {
            @Override
            public Select<CitiesRecord> create(DSLContext context) {
                return context.selectFrom(CITIES).where(CITIES.SOCIETY.equal(DEFAULT_BYTE_ARRAY));
            }
        });


        builder(INSERT_SIEGE, new QueryBuilder<Insert>() {
            @Override
            public Insert create(DSLContext context) {
                return context.insertInto(SIEGES)
                        .set(SIEGES.UUID, DEFAULT_BYTE_ARRAY)
                        .set(SIEGES.SOCIETY, DEFAULT_BYTE_ARRAY)
                        .set(SIEGES.CITY, DEFAULT_BYTE_ARRAY)

                        .set(SIEGES.X, DEFAULT_SHORT)
                        .set(SIEGES.Y, DEFAULT_SHORT)
                        .set(SIEGES.Z, DEFAULT_SHORT)

                        .set(SIEGES.CREATED, DEFAULT_TIMESTAMP)
                        .set(SIEGES.WAGER, DEFAULT_BYTE_ARRAY);
            }
        });

        builder(DROP_SIEGE, new QueryBuilder<Delete<SiegesRecord>>() {
            @Override
            public Delete<SiegesRecord> create(DSLContext context) {
                return context.delete(SIEGES)
                        .where(SIEGES.UUID.equal(DEFAULT_BYTE_ARRAY));
            }
        });

        builder(SELECT_SIEGES_BY_CITY, new QueryBuilder<Select<SiegesRecord>>() {
            @Override
            public Select<SiegesRecord> create(DSLContext context) {
                return context.selectFrom(SIEGES).where(SIEGES.CITY
                        .equal(DEFAULT_BYTE_ARRAY));
            }
        });

        builder(SELECT_SIEGES_BY_SOCIETY, new QueryBuilder<Select<SiegesRecord>>() {
            @Override
            public Select<SiegesRecord> create(DSLContext context) {
                return context.selectFrom(SIEGES).where(SIEGES.SOCIETY
                        .equal(DEFAULT_BYTE_ARRAY));
            }
        });
    }
}
