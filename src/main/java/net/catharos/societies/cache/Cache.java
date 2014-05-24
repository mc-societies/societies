package net.catharos.societies.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.inject.Inject;
import net.catharos.groups.Inactivatable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Represents a MemberCache
 */
public abstract class Cache<K, V extends Inactivatable> extends CacheLoader<K, V> implements RemovalListener<K, V> {

    private final LoadingCache<K, V> cache;

    @Inject
    public Cache(int maxSize, int lifeTime, TimeUnit timeUnit) {
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(maxSize)
                .expireAfterAccess(lifeTime, timeUnit)
                .removalListener(this)
                .recordStats()
                .build(this);
    }

    public V get(K key) {
        return cache.getUnchecked(key);
    }

    @Override
    public abstract V load(@NotNull K key) throws Exception;

    public Map<K, V> asMap() {
        return cache.asMap();
    }
}
