package net.catharos.societies.member;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import net.catharos.groups.Member;
import net.catharos.groups.MemberCache;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Represents a MemberCache
 */
public class LoadingMemberCache implements MemberCache {

    public static final int MAX_CACHED = 250;

    public static final int MEMBER_LIFE_TIME = 2;

    private final LoadingCache<UUID, Member> cache;

    @Inject
    public LoadingMemberCache(CacheLoader<UUID, Member> cacheLoader) {
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(MAX_CACHED)
                .expireAfterAccess(MEMBER_LIFE_TIME, TimeUnit.HOURS)
                .recordStats()
                .build(cacheLoader);
    }


    @Override
    public Member getMember(UUID uuid) {
        return cache.getUnchecked(uuid);
    }
}
