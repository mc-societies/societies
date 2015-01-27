package org.societies.sieging.wager;

import org.societies.api.sieging.Wager;
import org.societies.groups.group.Group;

import java.util.UUID;

/**
 * Represents a EmptyWager
 */
public class EmptyWager implements Wager {
    @Override
    public UUID getUUID() {
        return null;
    }

    @Override
    public void apply(Group group) {

    }
}
