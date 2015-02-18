package org.societies.sieging.memory;

import org.societies.api.sieging.Besieger;
import org.societies.groups.ExtensionRoller;
import org.societies.groups.group.Group;

/**
 * Represents a SiegeExtensionRoller
 */
class MemorySiegeRoller implements ExtensionRoller<Group> {

    @Override
    public void roll(Group extensible) {
        MemoryBesieger besieger = new MemoryBesieger(extensible);
        extensible.add(Besieger.class, besieger);
    }
}
