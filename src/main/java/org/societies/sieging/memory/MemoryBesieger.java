package org.societies.sieging.memory;

import com.google.common.collect.ImmutableSet;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.City;
import org.societies.api.sieging.Land;
import org.societies.groups.group.Group;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a MemoryBesieger
 */
class MemoryBesieger implements Besieger {

    private final THashMap<String, City> cities = new THashMap<String, City>();
    private final Set<Land> lands = new THashSet<Land>();
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
    public void addCities(Iterable<City> cities) {
        for (City city : cities) {
            addCity(city);
        }
    }

    @Override
    public void removeCity(String name) {
        cities.remove(name);
    }

    @Override
    public Set<Land> getUnallocatedLands() {
        return lands;
    }

    @Override
    public void addUnallocatedLand(Land land) {
        lands.add(land);
    }

    @Override
    public void removeUnallocatedLand(Land land) {
        lands.remove(land);
    }

    @Override
    public void addUnallocatedLands(Collection<Land> lands) {
        this.lands.addAll(lands);
    }

    @Override
    public City getCity(String name) {
        return cities.get(name);
    }

    @Override
    public void removeCity(City city) {
        cities.remove(city.getName());
    }

    @Override
    public Set<City> getCities() {
        return ImmutableSet.copyOf(cities.values());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemoryBesieger)) return false;

        MemoryBesieger that = (MemoryBesieger) o;

        return getUUID().equals(that.getUUID());
    }

    @Override
    public int hashCode() {
        return getUUID().hashCode();
    }
}
