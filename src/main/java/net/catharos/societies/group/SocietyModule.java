package net.catharos.societies.group;

import com.google.common.cache.CacheLoader;
import com.google.inject.TypeLiteral;
import net.catharos.groups.Group;
import net.catharos.groups.GroupCache;
import net.catharos.lib.shank.AbstractModule;

import java.util.UUID;

/**
 * Represents a SocietyModule
 */
public class SocietyModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GroupCache.class).to(LoadingGroupCache.class);

        bind(new TypeLiteral<CacheLoader<UUID, Group>>() {}).to(GroupCacheLoader.class);
    }
}
