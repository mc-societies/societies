package net.catharos.societies.group.sql;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import gnu.trove.set.hash.THashSet;
import net.catharos.groups.Group;
import net.catharos.groups.GroupFactory;
import net.catharos.groups.GroupProvider;
import net.catharos.groups.GroupPublisher;
import net.catharos.lib.core.util.ByteUtil;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.database.layout.tables.records.SocietiesRecord;
import net.catharos.societies.group.SocietyException;
import org.jooq.Result;
import org.jooq.Select;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;

import static net.catharos.societies.group.sql.SocietyQueries.*;

/**
 * Represents a LoadingGroupProvider
 */
class SQLGroupController implements GroupProvider, GroupPublisher {
    private final SocietyQueries queries;
    private final ListeningExecutorService service;
    private final GroupFactory factory;

    @Inject
    public SQLGroupController(SocietyQueries queries, ListeningExecutorService service, GroupFactory factory) {
        this.queries = queries;
        this.service = service;
        this.factory = factory;
    }

    @Override
    public ListenableFuture<Group> getGroup(UUID uuid) {
        Select<SocietiesRecord> query = queries.getQuery(SELECT_SOCIETY_BY_UUID);
        query.bind(1, ByteUtil.toByteArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));

        return evaluateSingle(uuid, queries.query(service, query));
    }

    private ListenableFuture<Group> evaluateSingle(final UUID uuid, final ListenableFuture<Result<SocietiesRecord>> result) {
        return Futures.transform(result, new Function<Result<SocietiesRecord>, Group>() {

            @Nullable
            @Override
            public Group apply(@Nullable Result<SocietiesRecord> input) {
                if (input == null) {
                    return null;
                }

                if (input.isEmpty()) {
                    return createGroup(uuid);
                } else if (input.size() > 1) {
                    throw new SocietyException("There are more groups with the same uuid?!");
                }

                return createGroup(input.get(0));
            }
        });

    }

    private ListenableFuture<Set<Group>> evaluate(ListenableFuture<Result<SocietiesRecord>> result) {
        return Futures.transform(result, new Function<Result<SocietiesRecord>, Set<Group>>() {

            @Nullable
            @Override
            public Set<Group> apply(@Nullable Result<SocietiesRecord> input) {
                if (input == null) {
                    return null;
                }

                THashSet<Group> groups = new THashSet<Group>(input.size());

                for (SocietiesRecord record : input) {
                    groups.add(createGroup(record));
                }

                return groups;
            }
        });
    }

    private Group createGroup(SocietiesRecord record) {
        return factory.create(UUIDGen.toUUID(record.getUuid()), record.getName());
    }

    private Group createGroup(UUID uuid) {
        return factory.create(uuid, null); //todo null name
    }

    @Override
    public ListenableFuture<Set<Group>> getGroup(String name) {
        Select<SocietiesRecord> query = queries.getQuery(SELECT_SOCIETY_BY_NAME);
        query.bind(1, name);

        return evaluate(queries.query(service, query));
    }

    @Override
    public ListenableFuture<Set<Group>> getGroups() {
        Select<SocietiesRecord> query = queries.getQuery(SELECT_SOCIETIES);

        return evaluate(queries.query(service, query));
    }

    @Override
    public ListenableFuture<Group> publish(Group group) {
        return null;
    }
}
