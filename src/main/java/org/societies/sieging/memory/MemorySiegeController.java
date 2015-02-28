package org.societies.sieging.memory;

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
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.societies.api.sieging.*;
import org.societies.bridge.Location;
import org.societies.sieging.wager.EmptyWager;

import java.util.Set;
import java.util.UUID;

import static com.googlecode.cqengine.query.QueryFactory.equal;

/**
 * Represents a MemorySiegeController
 */
@Singleton
class MemorySiegeController implements SiegeController {

    IndexedCollection<Siege> sieges = CQEngine.newInstance();

    public static final Attribute<Siege, UUID> SIEGE_UUID = new SimpleAttribute<Siege, UUID>("siege_uuid") {
        @Override
        public UUID getValue(Siege siege) { return siege.getUUID(); }
    };

    public static final Attribute<Siege, Besieger> SIEGE_BESIEGER = new SimpleAttribute<Siege, Besieger>("siege_besieger") {
        @Override
        public Besieger getValue(Siege siege) { return siege.getBesieger(); }
    };

    public static final Attribute<Siege, City> SIEGE_CITY = new SimpleAttribute<Siege, City>("siege_city") {
        @Override
        public City getValue(Siege siege) { return siege.getCity(); }
    };

    public static final Attribute<Siege, Location> SIEGE_INITIATED_LOCATION = new SimpleAttribute<Siege, Location>("siege_city") {
        @Override
        public Location getValue(Siege siege) { return siege.getLocationInitiated(); }
    };


    {
        sieges.addIndex(HashIndex.onAttribute(SIEGE_UUID));

        sieges.addIndex(HashIndex.onAttribute(SIEGE_BESIEGER));
        sieges.addIndex(HashIndex.onAttribute(SIEGE_CITY));
        sieges.addIndex(HashIndex.onAttribute(SIEGE_INITIATED_LOCATION));
    }

    private final Provider<UUID> uuidProvider;
    private final CityProvider cityProvider;
    private final Duration startDuration;
    private final double minSigeDistance;

    @Inject
    MemorySiegeController(Provider<UUID> uuidProvider, CityProvider cityProvider,
                          @Named("sieging.start-duration") Duration startDuration,
                          @Named("sieging.min-distance") double minSiegeDistance) {
        this.uuidProvider = uuidProvider;
        this.cityProvider = cityProvider;
        this.startDuration = startDuration;
        this.minSigeDistance = minSiegeDistance;
    }

    @Override

    public Siege start(Besieger besieger, City city, Location location) {
        return start(besieger, city, location, new EmptyWager());
    }

    @Override
    public Siege start(Besieger besieger, City city, Location location, Wager wager) {

        //fixme radius
        if (city.getLocation().distance(location) + city.getLands().size() * 2 < minSigeDistance) {
            return null;
        }

        boolean applied = wager.apply(besieger.getGroup());

        if (!applied) {
            return null;
        }

        DateTime now = DateTime.now();
        MemorySiege siege = new MemorySiege(uuidProvider.get(), besieger, city, wager, location, now, now.plus(startDuration));
        sieges.add(siege);
        return siege;
    }

    @Override
    public Siege getSiege(UUID uuid) {
        ResultSet<Siege> retrieve = sieges.retrieve(equal(SIEGE_UUID, uuid));
        return Iterables.getOnlyElement(retrieve, null);
    }

    @Override
    public Siege getSiege(Location location) {
        ResultSet<Siege> retrieve = sieges.retrieve(equal(SIEGE_INITIATED_LOCATION, location));
        return Iterables.getOnlyElement(retrieve, null);
    }


    @Override
    public Set<Siege> getSieges(Location location) {
        Optional<City> city = cityProvider.getCity(location);

        if (!city.isPresent()) {
            return getSieges(city.get());
        }

        return null;
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
    public Siege getSiegeByAttacker(Besieger besieger) {
        ResultSet<Siege> retrieve = sieges.retrieve(equal(SIEGE_BESIEGER, besieger));
        return Iterables.getOnlyElement(retrieve);
    }

    @Override
    public void stop(Siege siege, Besieger winner) {
        City city = siege.getCity();
        Besieger owner = city.getOwner();
        Besieger attacker = siege.getBesieger();
        Wager wager = siege.getWager();

        if (owner.equals(winner)) {
            //City wasn't conquered -> wager to city's owner

            wager.fulfill(owner.getGroup());

            removeSiege(siege);
        } else if (attacker.equals(winner)) {
            //City was conquered -> besieger get wager back and city

            wager.fulfill(attacker.getGroup());
            owner.removeCity(city);
            attacker.addCity(city);

            removeSiege(siege);
        } else {
            throw new IllegalArgumentException("Winner can't be a winner!");
        }
    }

    private void removeSiege(Siege siege) {
        sieges.remove(siege);
    }
}
