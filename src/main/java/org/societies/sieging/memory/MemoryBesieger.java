package org.societies.sieging.memory;

import gnu.trove.set.hash.THashSet;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.City;
import org.societies.groups.group.Group;

import java.util.Set;

/**
 * Represents a MemoryBesieger
 */
class MemoryBesieger implements Besieger {

    private final Set<City> cities = new THashSet<City>();
    private final Group group;

    public MemoryBesieger(Group group) {
        this.group = group;
    }

    @Override
    public Group getGroup() {
        return group;
    }

    @Override
    public void addCity(City city) {
        cities.add(city);
    }

    @Override
    public void removeCity(City city) {
        cities.add(city);
    }

    @Override
    public Set<City> getCities() {
        return cities;
    }
}
