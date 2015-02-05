package org.societies.sieging.memory;

import com.google.inject.Singleton;
import org.societies.api.sieging.*;
import org.societies.bridge.Location;
import org.societies.groups.group.Group;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a MemorySiegeController
 */
@Singleton
class MemorySiegeController implements SiegeController {
    @Override
    public Siege start(Besieger besieger, City city, Location location) {
        return null;
    }

    @Override
    public Siege start(Besieger besieger, City city, Location location, Wager wager) {
        return null;
    }

    @Override
    public Siege getSiege(UUID uuid) {
        return null;
    }

    @Override
    public Set<Siege> getSieges(City city) {
        return null;
    }

    @Override
    public Set<Siege> getSieges(Besieger besieger) {
        return null;
    }

    @Override
    public Set<Siege> getSiegesAgainst(Group victim) {
        return null;
    }

    @Override
    public void stop(Siege siege) {

    }
}
