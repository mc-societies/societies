package org.societies.sieging;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.BesiegerProvider;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupProvider;

import java.util.UUID;

/**
 * Represents a DefaultBesiegerProvider
 */
public class DefaultBesiegerProvider implements BesiegerProvider {

    private final GroupProvider groupProvider;

    @Inject
    public DefaultBesiegerProvider(GroupProvider groupProvider) {this.groupProvider = groupProvider;}

    @Override
    public Optional<Besieger> getBesieger(UUID uuid) {
        Optional<Group> group = groupProvider.getGroup(uuid);

        if (group.isPresent()) {
            return Optional.fromNullable(group.get().get(Besieger.class));
        }

        return Optional.absent();
    }
}
