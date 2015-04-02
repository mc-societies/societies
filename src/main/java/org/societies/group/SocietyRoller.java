package org.societies.group;

import com.google.inject.Inject;
import org.societies.api.group.Society;
import org.societies.groups.ExtensionRoller;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupPublisher;

/**
 * Represents a SocietyRoller
 */
class SocietyRoller implements ExtensionRoller<Group> {

    private final GroupPublisher groupPublisher;

    @Inject
    SocietyRoller(GroupPublisher groupPublisher) {
        this.groupPublisher = groupPublisher;
    }

    @Override
    public void roll(Group extensible) {
        extensible.add(Society.class, new MemorySociety(extensible, groupPublisher));
    }
}
