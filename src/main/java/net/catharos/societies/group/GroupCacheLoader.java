package net.catharos.societies.group;

import com.google.common.cache.CacheLoader;
import net.catharos.groups.Group;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a SocietyCacheLoader
 */
public class GroupCacheLoader extends CacheLoader<UUID, Group> {

    @Override
    public Group load(@NotNull UUID key) throws Exception {
        return null;
    }
}
