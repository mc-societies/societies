package org.societies.sieging.memory;

import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.BesiegerPublisher;
import org.societies.groups.group.GroupPublisher;

/**
 * Represents a BesiegerPublisher
 */
class MemoryBesiegerPublisher implements BesiegerPublisher {

    private final GroupPublisher groupPublisher;

    public MemoryBesiegerPublisher(GroupPublisher groupPublisher) {
        this.groupPublisher = groupPublisher;
    }

    @Override
    public Besieger publish(final Besieger besieger) {
        return groupPublisher.publish(besieger.getGroup()).get(Besieger.class);
    }
}
