package net.catharos.societies.group;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.catharos.groups.Group;
import net.catharos.groups.GroupProvider;
import net.catharos.societies.cache.Cache;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Represents a SocietyCache
 */
@Singleton
public class GroupCache extends Cache<Group> implements GroupProvider {

    public static final int MAX_CACHED = 50;

    public static final int SOCIETY_LIFE_TIME = 2;

    private final GroupProvider sourceProvider;

    @Inject
    public GroupCache(@Named("source-group-provider") GroupProvider sourceProvider) {
        super(MAX_CACHED, SOCIETY_LIFE_TIME, TimeUnit.HOURS);
        this.sourceProvider = sourceProvider;
    }

    @Override
    public Group getGroup(UUID uuid) {
        return get(uuid);
    }

    @Override
    public Group getGroup(String name) {
        return null; //fixme add lookup by name
    }

    @Override
    public Iterable<Group> getGroups() {
        return asMap().values();
    }

    @Override
    public Group load(@NotNull UUID uuid) throws Exception {
        return sourceProvider.getGroup(uuid);
    }
}
