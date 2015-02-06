package org.societies.sieging.memory;

import com.google.common.collect.ImmutableSet;
import gnu.trove.map.hash.THashMap;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.City;
import org.societies.groups.group.Group;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a MemoryBesieger
 */
class MemoryBesieger implements Besieger {

    private final THashMap<String, City> cities = new THashMap<String, City>();
    private final Group group;

    public MemoryBesieger(Group group) {
        this.group = group;
    }

    @Override
    public UUID getUUID() {
        return group.getUUID();
    }

    @Override
    public Group getGroup() {
        return group;
    }

    @Override
    public void addCity(City city) {
        cities.put(city.getName(), city);
    }

    @Override
    public void removeCity(City city) {
        cities.put(city.getName(), city);
    }

    @Override
    public Set<City> getCities() {
        return ImmutableSet.copyOf(cities.values());
    }
}
