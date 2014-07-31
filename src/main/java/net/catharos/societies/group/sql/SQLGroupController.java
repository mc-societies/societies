package net.catharos.societies.group.sql;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import gnu.trove.set.hash.THashSet;
import net.catharos.groups.*;
import net.catharos.lib.core.util.ByteUtil;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.database.layout.tables.records.SocietiesRecord;
import net.catharos.societies.group.SocietyException;
import net.catharos.societies.member.SocietyMember;
import org.jooq.*;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static net.catharos.societies.group.sql.SocietyQueries.*;

/**
 * Represents a LoadingGroupProvider
 */
class SQLGroupController implements GroupProvider, GroupPublisher {
    private final SocietyQueries queries;
    private final ListeningExecutorService service;
    private final GroupFactory factory;
    private final MemberProvider<SocietyMember> memberController;

    @Inject
    public SQLGroupController(SocietyQueries queries, ListeningExecutorService service, GroupFactory factory, MemberProvider<SocietyMember> memberController) {
        this.queries = queries;
        this.service = service;
        this.factory = factory;
        this.memberController = memberController;
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

                Group group = createGroup(input.get(0));

                // Get members
                Select<Record1<byte[]>> query = queries.getQuery(SocietyQueries.SELECT_SOCIETY_MEMBERS);

                for (Record1<byte[]> member : query.fetch()) {
                    try {
                        group.addMember(memberController.getMember(UUIDGen.toUUID(member.value1())).get());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                return group;
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
    public ListenableFuture<Group> publish(final Group group) {
        return service.submit(new Callable<Group>() {
            @Override
            public Group call() throws Exception {
                Insert<SocietiesRecord> query = queries.getQuery(SocietyQueries.INSERT_SOCIETY);

                query.bind(1, UUIDGen.toByteArray(group.getUUID()));

                query.bind(2, group.getName());
                query.bind(3, group.getName()); //todo tag

                query.execute();

                return group;
            }
        });
    }

    @Override
    public ListenableFuture<?> drop(final Group group) {
        return service.submit(new Runnable() {
            @Override
            public void run() {
                Query query = queries.getQuery(SocietyQueries.DROP_SOCIETY_BY_UUID);

                query.bind(1, UUIDGen.toByteArray(group.getUUID()));

                query.execute();
            }
        });
    }
}
