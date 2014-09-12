package net.catharos.societies.database.sql;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Inject;
import gnu.trove.set.hash.THashSet;
import net.catharos.groups.*;
import net.catharos.groups.rank.Rank;
import net.catharos.groups.rank.RankFactory;
import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.SettingProvider;
import net.catharos.groups.setting.subject.Subject;
import net.catharos.groups.setting.target.SimpleTarget;
import net.catharos.groups.setting.target.Target;
import net.catharos.lib.core.util.ByteUtil;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.database.layout.tables.records.SocietiesRecord;
import net.catharos.societies.group.SocietyException;
import net.catharos.societies.member.SocietyMember;
import org.jooq.*;
import org.jooq.types.UShort;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static net.catharos.societies.database.sql.SQLQueries.*;

/**
 * Represents a LoadingMemberProvider
 */
class SQLGroupController implements GroupProvider, GroupPublisher {

    public static final int PREPARE = DefaultGroup.PREPARE;

    private final SQLQueries queries;
    private final ListeningExecutorService service;
    private final GroupFactory groupFactory;
    private final SettingProvider settingProvider;
    private final RankFactory rankFactory;

    private final MemberProvider<SocietyMember> memberProvider;


    @Inject
    public SQLGroupController(SQLQueries queries,
                              ListeningExecutorService service,
                              GroupFactory groupFactory,
                              SettingProvider settingProvider,
                              RankFactory rankFactory,
                              MemberProvider<SocietyMember> memberProvider) {
        this.queries = queries;
        this.service = service;
        this.groupFactory = groupFactory;
        this.settingProvider = settingProvider;
        this.rankFactory = rankFactory;
        this.memberProvider = memberProvider;
    }


    //================================================================================
    // Groups
    //================================================================================

    @Override
    public ListenableFuture<Group> getGroup(UUID uuid) {
        return getGroup(uuid, null, service);
    }

    @Override
    public ListenableFuture<Group> getGroup(UUID uuid, Member predefined, ListeningExecutorService service) {
        Select<SocietiesRecord> query = queries.getQuery(SELECT_SOCIETY_BY_UUID);
        query.bind(1, ByteUtil.toByteArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));

        return querySingleGroup(uuid, queries.query(service, query), predefined);
    }

    private ListenableFuture<Group> querySingleGroup(final UUID uuid, final ListenableFuture<Result<SocietiesRecord>> result, final Member predefined) {
        return Futures.transform(result, new Function<Result<SocietiesRecord>, Group>() {

            @Nullable
            @Override
            public Group apply(@Nullable Result<SocietiesRecord> input) {
                if (input == null) {
                    return null;
                }

                if (input.isEmpty()) {
                    return groupFactory.create(uuid, Group.NEW_GROUP_NAME, Group.NEW_GROUP_TAG);
                }

                SocietiesRecord record = Iterables.getOnlyElement(input);

                return evaluateSingleGroup(record, predefined);
            }
        },MoreExecutors.sameThreadExecutor());

    }

    private Group evaluateSingleGroup(SocietiesRecord record, Member predefined) {
        byte[] uuid = record.getUuid();
        Group group = groupFactory.create(UUIDGen.toUUID(uuid), record.getName(), record.getTag());
        // Preparing
        group.setState(PREPARE);

        try {
            // Load members
            Select<Record1<byte[]>> query = queries.getQuery(SQLQueries.SELECT_SOCIETY_MEMBERS);

            query.bind(1, uuid);

            for (Record1<byte[]> member : query.fetch()) {
                try {
                    UUID memberUUID = UUIDGen.toUUID(member.value1());
                    Member memberToAdd;

                    if (predefined != null && predefined.getUUID().equals(memberUUID)) {
                        memberToAdd = predefined;
                    } else {
                        memberToAdd = memberProvider.getMember(memberUUID, group, MoreExecutors.sameThreadExecutor()).get();
                    }

                    group.addMember(memberToAdd);
                } catch (InterruptedException e) {
                    throw new SocietyException(e, "Failed to add member to group!");
                } catch (ExecutionException e) {
                    throw new SocietyException(e, "Failed to add member to group!");
                }
            }

            //Load settings
            loadSettings(group, uuid, queries.getQuery(SQLQueries.SELECT_SOCIETY_SETTINGS));

            //Load ranks
            Select<Record2<byte[], String>> rankQuery = queries.getQuery(SQLQueries.SELECT_GROUP_RANKS);
            rankQuery.bind(1, uuid);


            for (Record2<byte[], String> rankRecord : rankQuery.fetch()) {
                Rank rank = loadRank(rankRecord);
                group.addRank(rank);
            }

        } finally {
            // Finished
            group.setState(record.getState());
        }

        return group;
    }

    private Rank loadRank(Record2<byte[], String> rankRecord) {
        Rank rank = rankFactory.create(UUIDGen.toUUID(rankRecord.value1()), rankRecord.value2(), 0);
        rank.setState(PREPARE);

        loadSettings(rank, rankRecord.value1(), queries.getQuery(SQLQueries.SELECT_RANK_SETTINGS));

        rank.setState(0);
        return rank;
    }

    private void loadSettings(Subject subject, byte[] uuid, Select<Record3<byte[], UShort, byte[]>> query) {
        query.bind(1, uuid);

        for (Record3<byte[], UShort, byte[]> settingRecord : query.fetch()) {
            int settingID = settingRecord.value2().intValue();

            Setting setting = settingProvider.getSetting(settingID);

            if (setting == null) {
                //invalid setting
                continue;
            }

            byte[] targetUUID = settingRecord.value1();
            Target target;

            if (targetUUID == null) {
                target = subject;
            } else {
                target = new SimpleTarget(UUIDGen.toUUID(targetUUID));
            }

            Object value = setting.convert(settingRecord.value3());

            subject.set(setting, target, value);
        }
    }

    @Override
    public ListenableFuture<Set<Group>> getGroup(String name) {
        Select<SocietiesRecord> query = queries.getQuery(SELECT_SOCIETY_BY_NAME);
        query.bind(1, name);

        return evaluateMultipleGroups(queries.query(service, query));
    }

    private ListenableFuture<Set<Group>> evaluateMultipleGroups(ListenableFuture<Result<SocietiesRecord>> result) {
        return Futures.transform(result, new Function<Result<SocietiesRecord>, Set<Group>>() {

            @Nullable
            @Override
            public Set<Group> apply(@Nullable Result<SocietiesRecord> input) {
                if (input == null) {
                    return null;
                }

                THashSet<Group> groups = new THashSet<Group>(input.size());

                for (SocietiesRecord record : input) {
                    groups.add(evaluateSingleGroup(record, null));
                }

                return groups;
            }
        });
    }

    @Override
    public ListenableFuture<Set<Group>> getGroups() {
        return evaluateMultipleGroups(queries.query(service, SELECT_SOCIETIES));
    }

    //================================================================================
    // Publisher
    //================================================================================

    @Override
    public ListenableFuture<Group> publish(final Group group) {
        return service.submit(new Callable<Group>() {
            @Override
            public Group call() throws Exception {
                Insert<SocietiesRecord> query = queries.getQuery(SQLQueries.INSERT_SOCIETY);

                query.bind(1, UUIDGen.toByteArray(group.getUUID()));

                query.bind(2, group.getName());
                query.bind(3, group.getTag());

                query.execute();

                return group;
            }
        });
    }

    //================================================================================
    // Drop
    //================================================================================

    @Override
    public ListenableFuture<?> drop(final Group group) {
        return service.submit(new Runnable() {
            @Override
            public void run() {
                Query query = queries.getQuery(SQLQueries.DROP_SOCIETY_BY_UUID);

                query.bind(1, UUIDGen.toByteArray(group.getUUID()));

                query.execute();
            }
        });
    }
}
