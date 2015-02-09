package org.societies.sieging.memory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.googlecode.cqengine.CQEngine;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.resultset.ResultSet;
import org.joda.time.DateTime;
import org.societies.api.sieging.*;
import org.societies.bridge.Location;
import org.societies.groups.group.Group;
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


    {
        sieges.addIndex(HashIndex.onAttribute(SIEGE_UUID));

        sieges.addIndex(HashIndex.onAttribute(SIEGE_BESIEGER));
        sieges.addIndex(HashIndex.onAttribute(SIEGE_CITY));
    }

    private final Provider<UUID> uuidProvider;

    @Inject
    MemorySiegeController(Provider<UUID> uuidProvider) {this.uuidProvider = uuidProvider;}

    @Override

    public Siege start(Besieger besieger, City city, Location location) {
        return start(besieger, city, location, new EmptyWager());
    }

    @Override
    public Siege start(Besieger besieger, City city, Location location, Wager wager) {
        MemorySiege siege = new MemorySiege(uuidProvider.get(), besieger, city, wager, location, DateTime.now());
        sieges.add(siege);
        return siege;
    }

    @Override
    public Siege getSiege(UUID uuid) {
        ResultSet<Siege> retrieve = sieges.retrieve(equal(SIEGE_UUID, uuid));
        return Iterables.getOnlyElement(retrieve, null);
    }

    @Override
    public Set<Siege> getSieges(City city) {
        ResultSet<Siege> retrieve = sieges.retrieve(equal(SIEGE_CITY, city));
        return ImmutableSet.copyOf(retrieve);
    }

    @Override
    public Set<Siege> getSieges(Besieger besieger) {
        ResultSet<Siege> retrieve = sieges.retrieve(equal(SIEGE_BESIEGER, besieger));
        return ImmutableSet.copyOf(retrieve);
    }

    @Override
    public void stop(Siege siege) {
        sieges.remove(siege);
    }
}
