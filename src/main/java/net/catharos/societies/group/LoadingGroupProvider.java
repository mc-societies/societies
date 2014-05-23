package net.catharos.societies.group;

import com.google.inject.Provider;
import net.catharos.groups.Group;
import net.catharos.groups.GroupFactory;
import net.catharos.groups.GroupProvider;
import net.catharos.lib.core.util.ByteUtil;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.SocietiesQueries;
import net.catharos.societies.database.layout.tables.records.SocietiesRecord;
import org.jooq.Result;
import org.jooq.Select;

import java.util.UUID;

/**
 * Represents a LoadingGroupProvider
 */
public class LoadingGroupProvider implements GroupProvider {
    private final SocietiesQueries queries;
    private final Provider<Group> groupProvider;
    private final GroupFactory factory;

    public LoadingGroupProvider(SocietiesQueries queries, Provider<Group> groupProvider, GroupFactory factory) {
        this.queries = queries;
        this.groupProvider = groupProvider;
        this.factory = factory;
    }

    @Override
    public Group getGroup(UUID uuid) {
        Select<SocietiesRecord> query = queries.getQuery(SocietiesQueries.SELECT_SOCIETY);
        query.bind(1, ByteUtil.toByteArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));

        Result<SocietiesRecord> result;

        try {
            result = query.fetch();
        } catch (RuntimeException e) {
            throw new SocietyException(e, "Query failed to execute!");
        }

        if (result.isEmpty()) {
            return groupProvider.get();
        } else if (result.size() > 1) {
            throw new SocietyException("There are more groups with the same uuid?!");
        }

        SocietiesRecord record = result.get(0);

        return factory.create(UUIDGen.toUUID(record.getUuid()), record.getName());
    }

    @Override
    public Group getGroup(String name) {
        return null; //fixme add lookup by name
    }

    @Override
    public Iterable<Group> getGroups() {
        return null;
    }
}
