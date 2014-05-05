package net.catharos.societies.member;

import com.google.common.cache.CacheLoader;
import com.google.inject.TypeLiteral;
import net.catharos.groups.Member;
import net.catharos.groups.MemberCache;
import net.catharos.lib.shank.AbstractModule;

import java.util.UUID;

/**
 * Represents a MemberModule
 */
public class MemberModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MemberCache.class).to(LoadingMemberCache.class);

        bind(new TypeLiteral<CacheLoader<UUID, Member>>() {}).to(MemberCacheLoader.class);
    }
}
