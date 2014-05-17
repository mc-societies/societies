package net.catharos.societies.group;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import net.catharos.groups.Group;
import net.catharos.groups.GroupCache;
import net.catharos.groups.GroupFactory;
import net.catharos.lib.core.util.ByteUtil;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.SocietiesQueries;
import net.catharos.societies.cache.Cache;
import net.catharos.societies.database.layout.tables.records.SocietiesRecord;
import net.catharos.societies.member.MemberException;
import org.jetbrains.annotations.NotNull;
import org.jooq.Result;
import org.jooq.Select;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Represents a SocietyCache
 */
@Singleton
public class LoadingGroupCache extends Cache<Group> implements GroupCache {

    public static final int MAX_CACHED = 50;

    public static final int SOCIETY_LIFE_TIME = 2;

    private final SocietiesQueries queries;
    private final Provider<Group> groupProvider;
    private final GroupFactory factory;

    @Inject
    public LoadingGroupCache(SocietiesQueries queries, Provider<Group> groupProvider, GroupFactory factory) {
        super(MAX_CACHED, SOCIETY_LIFE_TIME, TimeUnit.HOURS);
        this.queries = queries;
        this.groupProvider = groupProvider;
        this.factory = factory;
    }

    @Override
    public Group getGroup(UUID uuid) {
        return get(uuid);
    }

    @Override
    public Group load(@NotNull UUID uuid) throws Exception {
        // Select record from database
        Select<SocietiesRecord> query = queries.getQuery(SocietiesQueries.SELECT_SOCIETY);
        query.bind(1, ByteUtil.toByteArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));

        // Check result
        Result<SocietiesRecord> result;

        try {
            result = query.fetch();
        } catch (RuntimeException e) {
            throw new MemberException(uuid, e, "Query failed to execute!");
        }

        if (result.isEmpty()) {
            return groupProvider.get();
        } else if (result.size() > 1) {
            throw new MemberException(uuid, "There are more groups with the same uuid?!");
        }

        SocietiesRecord record = result.get(0);

        return factory.create(UUIDGen.toUUID(record.getUuid()));
    }
}
