package org.societies.sieging.memory;

import algs.model.twod.TwoDPoint;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.googlecode.cqengine.CQEngine;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.index.suffix.SuffixTreeIndex;
import com.googlecode.cqengine.resultset.ResultSet;
import org.joda.time.DateTime;
import org.societies.api.sieging.*;
import org.societies.bridge.Location;
import org.societies.sieging.memory.index.KDTreeIndex;

import java.io.IOException;
import java.util.UUID;

import static com.googlecode.cqengine.query.QueryFactory.equal;
import static com.googlecode.cqengine.query.QueryFactory.startsWith;
import static org.societies.sieging.memory.index.Nearest.nearest;

/**
 * Represents a MemoryCityProvider
 */
@Singleton
class MemoryCityController implements CityProvider, CityPublisher {

    IndexedCollection<City> cities = CQEngine.newInstance();

    public static final Attribute<City, UUID> CITY_UUID = new SimpleAttribute<City, UUID>("city_uuid") {
        @Override
        public UUID getValue(City city) { return city.getUUID(); }
    };

    public static final Attribute<City, String> CITY_NAME = new SimpleAttribute<City, String>("city_name") {
        @Override
        public String getValue(City city) {
            return city.getName();
        }
    };

    public static final SimpleAttribute<City, TwoDPoint> CITY_NEAREST = new SimpleAttribute<City, TwoDPoint>("city_lands") {
        @Override
        public TwoDPoint getValue(City city) {
            Location location = city.getLocation();
            return new TwoDPoint(location.getX(), location.getZ());
        }
    };

    {
        cities.addIndex(HashIndex.onAttribute(CITY_UUID));

        cities.addIndex(HashIndex.onAttribute(CITY_NAME));
        cities.addIndex(SuffixTreeIndex.onAttribute(CITY_NAME));

        cities.addIndex(KDTreeIndex.onAttribute(2, CITY_NEAREST));
    }

    IndexedCollection<Land> lands = CQEngine.newInstance();

    public static final Attribute<Land, UUID> LAND_UUID = new SimpleAttribute<Land, UUID>("land_uuid") {
        @Override
        public UUID getValue(Land land) {
            return land.getUUID();
        }
    };

    {
        lands.addIndex(HashIndex.onAttribute(LAND_UUID));
    }

    private final Provider<UUID> uuidProvider;
    private final CityWriter cityWriter;

    @Inject
    MemoryCityController(Provider<UUID> uuidProvider, CityWriter cityWriter) {
        this.uuidProvider = uuidProvider;
        this.cityWriter = cityWriter;
    }

    @Override
    public City getCity(UUID uuid) {
        ResultSet<City> retrieve = cities.retrieve(equal(CITY_UUID, uuid));

        return Iterables.getOnlyElement(retrieve, null);
    }

    @Override
    public City getCity(String name) {
        ResultSet<City> retrieve = cities.retrieve(startsWith(CITY_NAME, name));

        return Iterables.getOnlyElement(retrieve, null);
    }

    @Override
    public City getCity(Location location) {
        ResultSet<City> retrieve = cities.retrieve(nearest(CITY_NEAREST, new TwoDPoint(location.getX(), location.getZ())));

        City city = Iterables.getOnlyElement(retrieve, null);

        if (city != null) {
            int lands = city.getLands().size();

            if (city.getLocation().distance(location) > lands * 2) {
                return null;
            }
        }

        return city;
    }

    @Override
    public City publish(String name, Location cityLocation, Besieger group) {
        City published = publish(new MemoryCity(uuidProvider.get(), name, cityLocation, group, DateTime.now(), this));
        published.link();
        return published;
    }

    @Override
    public City publish(City city) {
        cities.add(city);
        try {
            cityWriter.writeCities(city.getOwner());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return city;
    }
}
