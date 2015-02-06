package org.societies.sieging.memory;

import com.google.inject.Inject;
import org.societies.api.sieging.Besieger;
import org.societies.groups.ExtensionRoller;
import org.societies.groups.group.Group;

import java.io.IOException;

/**
* Represents a SiegeExtensionRoller
*/
class MemorySiegeRoller implements ExtensionRoller<Group> {

    private final CityParser mapper;

    @Inject
    MemorySiegeRoller(CityParser mapper) {this.mapper = mapper;}

    @Override
    public void roll(Group extensible) {
        MemoryBesieger besieger = new MemoryBesieger(extensible);
        extensible.add(Besieger.class, besieger);

        try {
            mapper.readCities(besieger);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
