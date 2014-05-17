package net.catharos.societies.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Represents a MemberCache
 */
public abstract class Cache<V> extends CacheLoader<UUID, V> {

    private final LoadingCache<UUID, V> cache;

    @Inject
    public Cache(int maxSize, int lifeTime, TimeUnit timeUnit) {
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(maxSize)
                .expireAfterAccess(lifeTime, timeUnit)
                .recordStats()
                .build(this);
    }

    public V get(UUID key) {
        return cache.getUnchecked(key);
    }

    @Override
    public abstract V load(@NotNull UUID uuid) throws Exception;
}
