package org.societies.sieging;

import com.google.inject.Inject;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.BesiegerPublisher;
import org.societies.groups.group.GroupPublisher;

/**
 * Represents a BesiegerPublisher
 */
public class DefaultBesiegerPublisher implements BesiegerPublisher {

    private final GroupPublisher groupPublisher;

    @Inject
    public DefaultBesiegerPublisher(GroupPublisher groupPublisher) {
        this.groupPublisher = groupPublisher;
    }

    @Override
    public Besieger publish(final Besieger besieger) {
        return groupPublisher.publish(besieger.getGroup()).get(Besieger.class);
    }
}
