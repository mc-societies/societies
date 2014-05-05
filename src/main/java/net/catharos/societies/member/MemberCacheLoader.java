package net.catharos.societies.member;

import com.google.common.cache.CacheLoader;
import net.catharos.groups.Member;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a MemberCacheLoader
 */
public class MemberCacheLoader extends CacheLoader<UUID, Member> {

    @Override
    public Member load(@NotNull UUID key) throws Exception {
        return null;
    }
}
