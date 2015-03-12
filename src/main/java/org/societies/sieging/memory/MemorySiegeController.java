package org.societies.sieging.memory;

import algs.model.twod.TwoDPoint;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.googlecode.cqengine.CQEngine;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.resultset.ResultSet;
import net.catharos.lib.core.uuid.UUIDGen;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.societies.api.sieging.*;
import org.societies.bridge.Location;
import org.societies.sieging.memory.index.KDTreeIndex;
import org.societies.sieging.wager.EmptyWager;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static com.googlecode.cqengine.query.QueryFactory.equal;
import static org.societies.sieging.memory.index.Nearest.nearest;

/**
 * Represents a MemorySiegeController
 */
@Singleton
class MemorySiegeController implements SiegeController {

    IndexedCollection<Siege> sieges = CQEngine.newInstance();

//    public static final Attribute<Siege, UUID> SIEGE_UUID = new SimpleAttribute<Siege, UUID>("siege_uuid") {
//        @Override
//        public UUID getValue(Siege siege) { return siege.getUUID(); }
//    };

    public static final Attribute<Siege, Besieger> SIEGE_BESIEGER = new SimpleAttribute<Siege, Besieger>("siege_besieger") {
        @Override
        public Besieger getValue(Siege siege) { return siege.getBesieger(); }
    };

    public static final Attribute<Siege, City> SIEGE_CITY = new SimpleAttribute<Siege, City>("siege_city") {
        @Override
        public City getValue(Siege siege) { return siege.getCity(); }
    };

    public static final Attribute<Siege, Location> SIEGE_INITIATED_LOCATION_HASH = new SimpleAttribute<Siege, Location>("siege_initiated") {
        @Override
        public Location getValue(Siege siege) { return siege.getLocationInitiated(); }
    };

    public static final SimpleAttribute<Siege, TwoDPoint> SIEGE_INITIATED_LOCATION = new SimpleAttribute<Siege, TwoDPoint>("siege_") {
        @Override
        public TwoDPoint getValue(Siege siege) {
            Location location = siege.getLocationInitiated();
            return new TwoDPoint(location.getX(), location.getZ());
        }
    };


    {
//        sieges.addIndex(HashIndex.onAttribute(SIEGE_UUID));

        sieges.addIndex(HashIndex.onAttribute(SIEGE_BESIEGER));
        sieges.addIndex(HashIndex.onAttribute(SIEGE_CITY));
        sieges.addIndex(HashIndex.onAttribute(SIEGE_INITIATED_LOCATION_HASH));
        sieges.addIndex(KDTreeIndex.onAttribute(2, SIEGE_INITIATED_LOCATION));
    }

    private final Provider<UUID> uuidProvider;
    private final CityProvider cityProvider;
    private final Duration startDuration;
    private final int startLands;
    private final Logger logger;


    @Inject
    MemorySiegeController(Provider<UUID> uuidProvider, CityProvider cityProvider,
                          @Named("sieging.start-duration") Duration startDuration,
                          @Named("city.start-lands") int startLands,
                          Logger logger) {
        this.uuidProvider = uuidProvider;
        this.cityProvider = cityProvider;
        this.startDuration = startDuration;
        this.startLands = startLands;
        this.logger = logger;
    }

    @Override

    public Siege start(Besieger besieger, City city, Location location) {
        return start(besieger, city, location, new EmptyWager());
    }

    @Override
    public Siege start(Besieger besieger, City city, Location location, Wager wager) {

        DateTime now = DateTime.now();
        MemorySiege siege = new MemorySiege(uuidProvider.get(), besieger, city, wager, location, now, now.plus(startDuration));
        sieges.add(siege);
        return siege;
    }

//    @Override
//    public Optional<Siege> getSiege(UUID uuid) {
//        ResultSet<Siege> retrieve = sieges.retrieve(equal(SIEGE_UUID, uuid));
//        return Optional.fromNullable(Iterables.getOnlyElement(retrieve, null));
//    }

    @Override
    public Optional<Siege> getSiegeInitiatedAt(Location location) {
        ResultSet<Siege> retrieve = sieges.retrieve(equal(SIEGE_INITIATED_LOCATION_HASH, location));
        return Optional.fromNullable(Iterables.getOnlyElement(retrieve, null));
    }

    @Override
    public Optional<Siege> getSiegeInitiatedNear(Location location) {
        ResultSet<Siege> retrieve = sieges.retrieve(nearest(SIEGE_INITIATED_LOCATION, new TwoDPoint(location.getX(), location.getZ())));

        return Optional.fromNullable(Iterables.getOnlyElement(retrieve, null));
    }

    @Override
    public Set<Siege> getSiegeNear(Location location) {
        Optional<City> city = cityProvider.getCity(location);

        if (!city.isPresent()) {
            return getSieges(city.get());
        }

        return Collections.emptySet();
    }

    @Override
    public Set<Siege> getSieges(City city) {
        ResultSet<Siege> retrieve = sieges.retrieve(equal(SIEGE_CITY, city));
        return ImmutableSet.copyOf(retrieve);
    }

    /**
     * @param besieger The attacker
     * @return
     */
    @Override
    public Optional<Siege> getSiegeByAttacker(Besieger besieger) {
        ResultSet<Siege> retrieve = sieges.retrieve(equal(SIEGE_BESIEGER, besieger));
        return Optional.fromNullable(Iterables.getOnlyElement(retrieve, null));
    }

    @Override
    public void stop(Siege siege, Besieger winner) {
        City city = siege.getCity();
        Besieger owner = city.getOwner();
        Besieger attacker = siege.getBesieger();
        Wager wager = siege.getWager();

        System.out.println(winner.getGroup().getName() + " won!");

        if (owner.equals(winner)) {
            //City wasn't conquered -> wager to city's owner

            wager.fulfill(owner.getGroup());

            cancel(siege);
        } else if (attacker.equals(winner)) {
            //City was conquered -> besieger get wager back and city

            wager.fulfill(attacker.getGroup());
            owner.removeCity(city);
            attacker.addCity(city);
            city.setOwner(attacker);

            Collection<Land> lands = city.getLands();

            int toRemove = lands.size();

            owner.addUnallocatedLands(lands);
            city.clearLands();

            int removed = 0;

            for (City ownerCity : owner.getCities()) {
                for (Land land : ownerCity.getLands()) {
                    if (land.getOrigin().equals(city.getUUID())) {
                        removed++;
                        ownerCity.removeLand(land.getUUID());
                    }
                }
            }

            for (Land land : owner.getUnallocatedLands()) {
                if (land.getOrigin().equals(city.getUUID())) {
                    removed++;
                    owner.removeUnallocatedLand(land);
                }
            }

            if (toRemove != removed) {
                logger.warn("Failed to remove lands for " + owner.getGroup().getName());
            }


            for (int i = 0; i < startLands; i++) {
                city.addLand(new SimpleLand(UUIDGen.generateType1UUID(), city.getUUID()));
            }

            cancel(siege);
        } else {
            throw new IllegalArgumentException("Winner can't be a winner!");
        }
    }

    @Override
    public void cancel(Siege siege) {
        sieges.remove(siege);
    }
}
