package org.societies.sieging.memory;

import algs.model.twod.TwoDPoint;
import com.googlecode.cqengine.CQEngine;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.resultset.ResultSet;
import org.joda.time.DateTime;
import org.societies.api.sieging.City;
import org.societies.bridge.Location;
import org.societies.sieging.memory.index.KDTreeIndex;

import java.util.Random;

import static org.societies.sieging.memory.index.Nearest.nearest;

/**
 * Represents a Test
 */
public class Test {


    public static final SimpleAttribute<City, TwoDPoint> CITY_NEAREST = new SimpleAttribute<City, TwoDPoint>("city_lands") {
        @Override
        public TwoDPoint getValue(City city) {
            return new TwoDPoint(city.getLocation().getX(), city.getLocation().getZ());
        }
    };


    public static void main(String[] args) {
        IndexedCollection<City> cities = CQEngine.newInstance();

        cities.addIndex(KDTreeIndex.onAttribute(2, CITY_NEAREST));


        cities.add(new MemoryCity(null, "test", new Location(null, 10, 10, 10), null, DateTime.now(), null));
        cities.add(new MemoryCity(null, "test1", new Location(null, 1, 1, 1), null, DateTime.now(), null));

        for (int i = 0; i < 2000; i++) {
            cities.add(new MemoryCity(null, "test1", new Location(null, new Random().nextDouble() * 2000, new Random()
                    .nextDouble() * 2000, new Random().nextDouble() * 2000), null, DateTime.now(), null));
        }

        ResultSet<City> retrieve = cities.retrieve(nearest(CITY_NEAREST, new TwoDPoint(6, 6)));

        for (int i = 0; i < 500000; i++) {
            retrieve.size();
        }


        long start = System.nanoTime();
        for (int i = 0; i < 5000; i++) {
            retrieve.size();
        }
        long finish = System.nanoTime();
        System.out.printf("Check took %s!%n", (finish - start));

    }
}
