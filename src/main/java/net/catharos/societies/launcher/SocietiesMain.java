package net.catharos.societies.launcher;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.googlecode.cqengine.CQEngine;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.MultiValueAttribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.index.radixreversed.ReversedRadixTreeIndex;
import com.googlecode.cqengine.query.Query;
import net.catharos.lib.core.command.Commands;
import net.catharos.lib.core.command.ParsingException;
import net.catharos.lib.core.command.reflect.instance.CommandAnalyser;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.societies.SocietiesModule;
import net.catharos.societies.member.SocietyMember;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static com.googlecode.cqengine.query.QueryFactory.contains;

/**
 * Represents a SocietiesMain
 */
public class SocietiesMain {

    public static class Car {
        public final int carId;
        public final String name;
        public final String description;
        public final List<String> features;

        public Car(int carId, String name, String description, List<String> features) {
            this.carId = carId;
            this.name = name;
            this.description = description;
            this.features = features;
        }

        @Override
        public String toString() {
            return "Car{carId=" + carId + ", name='" + name + "', description='" + description + "', features=" + features + "}";
        }

        // -------------------------- Attributes --------------------------
        public static final Attribute<Car, Integer> CAR_ID = new SimpleAttribute<Car, Integer>("carId") {
            public Integer getValue(Car car) { return car.carId; }
        };

        public static final Attribute<Car, String> NAME = new SimpleAttribute<Car, String>("name") {
            public String getValue(Car car) { return car.name; }
        };

        public static final Attribute<Car, String> DESCRIPTION = new SimpleAttribute<Car, String>("description") {
            public String getValue(Car car) { return car.description; }
        };

        public static final Attribute<Car, String> FEATURES = new MultiValueAttribute<Car, String>("features") {
            public List<String> getValues(Car car) { return car.features; }
        };
    }

    public static void main(String[] args) throws ParsingException, SQLException {
        long start, finish;
//        ODatabaseDocumentTx db = new ODatabaseDocumentTx("plocal:/tmp/test");
////        db.create();
////        db.open("admin", "admin");
//
////        ODocument animal = new ODocument("Animal");
////        animal.field( "name", "Gaudi" );
////        animal.field( "location", "Madrid" );
////        animal.save();
//
//        System.out.println(db.query(new OSQLSynchQuery<ODocument>("select * from Animal where name like 'Gaudi'")));;
//
//        long start = System.nanoTime();
//        for (int i = 0; i < 5000; i++) {
//            db.query(new OSQLSynchQuery<ODocument>("select * from Animal where name like 'Gaudi'"));
//        }
//        long finish = System.nanoTime();
//        System.out.printf("Check took %s!%n", (finish - start));
//
//        try {
//            // YOUR CODE
//        } finally {
//            db.close();
//        }

        //================================================================================
        //
        //================================================================================


        IndexedCollection<Car> cars = CQEngine.newInstance();

        cars.addIndex(NavigableIndex.onAttribute(Car.CAR_ID));
        cars.addIndex(ReversedRadixTreeIndex.onAttribute(Car.NAME));

        cars.add(new Car(1, "ford focus", "great condition, low mileage", Arrays.asList("spare tyre", "sunroof")));
        cars.add(new Car(2, "ford taurus", "dirty and unreliable, flat tyre", Arrays.asList("spare tyre", "radio")));
        cars.add(new Car(3, "honda civic", "has a flat tyre and high mileage", Arrays.asList("radio")));

        Query<Car> query1 = contains(Car.NAME, "vic");

         start = System.nanoTime();
        for (int i = 0; i < 50000; i++) {
            cars.retrieve(query1);
        }
        finish = System.nanoTime();
        System.out.printf("Check took %s!%n", (finish - start));

        //================================================================================
        //
        //================================================================================

        Injector injector = Guice.createInjector(new SocietiesModule());

//        Group group = injector.getInstance(DefaultGroup.class);
//
//        injector.getInstance(SocietiesQueries.class);

        injector.getInstance(Key.get(new TypeLiteral<CommandAnalyser<Sender>>() {}));

        Commands<Sender> instance = injector
                .getInstance(Key.get(new TypeLiteral<Commands<Sender>>() {}, Names.named("global-command")));

        SocietyMember sender = injector.getInstance(SocietyMember.class);
        instance.parse(sender, "society create 5").execute();
    }
}
