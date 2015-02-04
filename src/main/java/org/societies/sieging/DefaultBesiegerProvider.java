package org.societies.sieging;

import com.google.inject.Inject;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.BesiegerProvider;
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
    public Besieger getBesieger(UUID uuid) {
        return groupProvider.getGroup(uuid).get(Besieger.class);
    }
}
