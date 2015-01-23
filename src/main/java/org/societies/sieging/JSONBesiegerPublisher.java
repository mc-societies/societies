package org.societies.sieging;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.BesiegerPublisher;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupPublisher;

import javax.annotation.Nullable;

/**
 * Represents a BesiegerPublisher
 */
public class JSONBesiegerPublisher implements BesiegerPublisher {

    private final GroupPublisher groupPublisher;

    public JSONBesiegerPublisher(GroupPublisher groupPublisher) {
        this.groupPublisher = groupPublisher;
    }

    @Override
    public ListenableFuture<Besieger> publish(final Besieger besieger) {
        return Futures.transform(groupPublisher.publish(besieger.getGroup()), new Function<Group, Besieger>() {
            @Nullable
            @Override
            public Besieger apply(Group input) {
                return besieger;
            }
        });
    }
}
