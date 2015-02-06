package org.societies.sieging.memory;

import com.google.inject.Inject;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.City;
import org.societies.groups.ExtensionRoller;
import org.societies.groups.group.Group;

import java.io.IOException;
import java.util.Set;

/**
 * Represents a SiegeExtensionRoller
 */
class MemorySiegeRoller implements ExtensionRoller<Group> {

    private final CityParser mapper;
    private final MemoryCityController controller;

    @Inject
    MemorySiegeRoller(CityParser mapper, MemoryCityController controller) {
        this.mapper = mapper;
        this.controller = controller;
    }

    @Override
    public void roll(Group extensible) {
        MemoryBesieger besieger = new MemoryBesieger(extensible);
        extensible.add(Besieger.class, besieger);


        try {
            Set<City> cities = mapper.readCities(besieger);
            controller.cities.addAll(cities);

            for (City city : cities) {
                besieger.addCity(city);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
