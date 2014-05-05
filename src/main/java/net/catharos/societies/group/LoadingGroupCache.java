package net.catharos.societies.group;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.catharos.groups.Group;
import net.catharos.groups.GroupCache;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Represents a SocietyCache
 */
@Singleton
public class LoadingGroupCache implements GroupCache {

    public static final int MAX_CACHED = 50;

    public static final int SOCIETY_LIFE_TIME = 2;

    private final LoadingCache<UUID, Group> cache;

    @Inject
    public LoadingGroupCache(CacheLoader<UUID, Group> cacheLoader) {
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(MAX_CACHED)
                .expireAfterAccess(SOCIETY_LIFE_TIME, TimeUnit.HOURS)
                .recordStats()
                .build(cacheLoader);
    }

    @Override
    public Group getGroup(UUID uuid) {
        return cache.getUnchecked(uuid);
    }
}
