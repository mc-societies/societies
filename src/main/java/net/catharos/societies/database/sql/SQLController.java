package net.catharos.societies.database.sql;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
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
import net.catharos.societies.PlayerProvider;
import net.catharos.societies.database.layout.tables.records.MembersRecord;
import net.catharos.societies.database.layout.tables.records.SocietiesRecord;
import net.catharos.societies.group.SocietyException;
import net.catharos.societies.member.MemberException;
import net.catharos.groups.MemberFactory;
import net.catharos.societies.member.SocietyMember;
import org.bukkit.entity.Player;
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
class SQLController implements MemberProvider<SocietyMember>, MemberPublisher<SocietyMember>, GroupProvider, GroupPublisher {

    private final PlayerProvider playerProvider;
    private final SQLQueries queries;
    private final ListeningExecutorService service;
    private final GroupFactory groupFactory;
    private final MemberFactory<SocietyMember> memberFactory;
    private final SettingProvider settingProvider;
    private final RankFactory rankFactory;

    @Inject
    public SQLController(PlayerProvider playerProvider,
                         SQLQueries queries,
                         ListeningExecutorService service,
                         GroupFactory groupFactory, MemberFactory<SocietyMember> memberFactory,
                         SettingProvider settingProvider,
                         RankFactory rankFactory) {
        this.playerProvider = playerProvider;
        this.queries = queries;
        this.service = service;
        this.groupFactory = groupFactory;
        this.memberFactory = memberFactory;
        this.settingProvider = settingProvider;
        this.rankFactory = rankFactory;
    }

    //================================================================================
    // Members
    //================================================================================

    @Override
    public ListenableFuture<SocietyMember> getMember(String name) {
        Player player = playerProvider.getPlayer(name);

        if (player == null) {
            return Futures.immediateCheckedFuture(null);
        }

        return getMember(player.getUniqueId());
    }

    @Override
    public ListenableFuture<SocietyMember> getMember(UUID uuid) {
        return getMember(uuid, null);
    }

    public ListenableFuture<SocietyMember> getMember(final UUID uuid, final Group group) {
        return queryMember(uuid, new Function<Result<MembersRecord>, SocietyMember>() {
            @Nullable
            @Override
            public SocietyMember apply(@Nullable Result<MembersRecord> input) {
                return evaluateMember(uuid, group, input);
            }
        });
    }

    private ListenableFuture<SocietyMember> queryMember(UUID uuid, Function<Result<MembersRecord>, SocietyMember> transformer) {
        Select<MembersRecord> query = queries.getQuery(SQLQueries.SELECT_MEMBER_BY_UUID);
        query.bind(1, ByteUtil.toByteArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));

        ListenableFuture<Result<MembersRecord>> future = queries.query(service, query);

        return Futures.transform(future, transformer);
    }

    private SocietyMember evaluateMember(UUID uuid, Group group, Result<MembersRecord> input) {
        if (input == null) {
            return null;
        }

        if (input.isEmpty()) {
            return memberFactory.create(uuid);
        } else if (input.size() > 1) {
            throw new MemberException(uuid, "There are more users with the same uuid?!");
        }

        MembersRecord record = input.get(0);

        SocietyMember member = memberFactory.create(UUIDGen.toUUID(record.getUuid()));
        member.setState(record.getState());

        // Load society
        byte[] rawSociety = record.getSociety();

        if (rawSociety == null || rawSociety.length != UUIDGen.UUID_LENGTH) {
            return member;
        }

        if (group == null) {
            try {
                member.setGroup(getGroup(UUIDGen.toUUID(rawSociety)).get());
            } catch (InterruptedException e) {
                throw new MemberException(uuid, e, "Failed to set group of member!");
            } catch (ExecutionException e) {
                throw new MemberException(uuid, e, "Failed to set group of member!");
            }
        } else {
            member.setGroup(group);
        }

        if (group != null) {
            //Load ranks
            Select<Record1<byte[]>> query = queries.getQuery(SQLQueries.SELECT_MEMBER_RANKS);
            query.bind(1, record.getUuid());

            for (Record1<byte[]> rankRecord : query.fetch()) {
                UUID rankUUID = UUIDGen.toUUID(rankRecord.value1());
                Rank rank = group.getRank(rankUUID);

                if (rank != null) {
                    member.addRank(rank);
                }
            }
        }

        return member;
    }

    //================================================================================
    // Publisher
    //================================================================================

    @Override
    public ListenableFuture<SocietyMember> publish(final SocietyMember member) {
        return service.submit(new Callable<SocietyMember>() {
            @Override
            public SocietyMember call() throws Exception {
                Insert<MembersRecord> query = queries.getQuery(SQLQueries.INSERT_MEMBER);

                query.bind(1, UUIDGen.toByteArray(member.getUUID()));

                Object group = null;

                if (member.getGroup() != null) {
                    group = UUIDGen.toByteArray(member.getGroup().getUUID());
                }
                query.bind(2, group);

                query.execute();

                return member;
            }
        });
    }

    //================================================================================
    // Drop
    //================================================================================

    @Override
    public ListenableFuture<?> drop(final SocietyMember member) {
        return service.submit(new Runnable() {
            @Override
            public void run() {
                Query query = queries.getQuery(SQLQueries.DROP_MEMBER_BY_UUID);

                query.bind(1, UUIDGen.toByteArray(member.getUUID()));

                query.execute();
            }
        });
    }

    //================================================================================
    // Groups
    //================================================================================

    @Override
    public ListenableFuture<Group> getGroup(UUID uuid) {
        Select<SocietiesRecord> query = queries.getQuery(SELECT_SOCIETY_BY_UUID);
        query.bind(1, ByteUtil.toByteArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));

        return querySingleGroup(uuid, queries.query(service, query));
    }

    private ListenableFuture<Group> querySingleGroup(final UUID uuid, final ListenableFuture<Result<SocietiesRecord>> result) {
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

                return evaluateSingleGroup(record);
            }
        });

    }

    private Group evaluateSingleGroup(SocietiesRecord record) {
        byte[] uuid = record.getUuid();
        Group group = groupFactory.create(UUIDGen.toUUID(uuid), record.getName(), record.getTag());
        group.setState(record.getState());

        // Load members
        Select<Record1<byte[]>> query = queries.getQuery(SQLQueries.SELECT_SOCIETY_MEMBERS);

        query.bind(1, uuid);

        for (Record1<byte[]> member : query.fetch()) {
            try {
                UUID memberUUID = UUIDGen.toUUID(member.value1());
                group.addMember(getMember(memberUUID, group).get());
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
            Rank rank = rankFactory.create(UUIDGen.toUUID(rankRecord.value1()), rankRecord.value2(), 0);
            loadSettings(rank, rankRecord.value1(), queries.getQuery(SQLQueries.SELECT_RANK_SETTINGS));
            group.addRank(rank);
        }
        return group;
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
                    groups.add(evaluateSingleGroup(record));
                }

                return groups;
            }
        });
    }

    @Override
    public ListenableFuture<Set<Group>> getGroups() {
        Select<SocietiesRecord> query = queries.getQuery(SELECT_SOCIETIES);

        return evaluateMultipleGroups(queries.query(service, query));
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
