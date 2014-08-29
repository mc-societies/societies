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
import net.catharos.groups.setting.target.SimpleTarget;
import net.catharos.groups.setting.target.Target;
import net.catharos.lib.core.util.ByteUtil;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.PlayerProvider;
import net.catharos.societies.database.layout.tables.records.MembersRecord;
import net.catharos.societies.database.layout.tables.records.RanksRecord;
import net.catharos.societies.database.layout.tables.records.SocietiesRecord;
import net.catharos.societies.group.SocietyException;
import net.catharos.societies.member.MemberException;
import net.catharos.societies.member.MemberFactory;
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
    private final MemberFactory memberFactory;
    private final SettingProvider settingProvider;
    private final RankFactory rankFactory;

    @Inject
    public SQLController(PlayerProvider playerProvider,
                         SQLQueries queries,
                         ListeningExecutorService service,
                         GroupFactory groupFactory, MemberFactory memberFactory,
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

    @Override
    public ListenableFuture<SocietyMember> getMember(UUID uuid) {
        return getMember(uuid, null);
    }

    private ListenableFuture<SocietyMember> prepare(UUID uuid, Function<Result<MembersRecord>, SocietyMember> transform) {
        Select<MembersRecord> query = queries.getQuery(SQLQueries.SELECT_MEMBER_BY_UUID);
        query.bind(1, ByteUtil.toByteArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));

        ListenableFuture<Result<MembersRecord>> future = queries.query(service, query);

        return Futures.transform(future, transform);
    }

    private SocietyMember evaluate(UUID uuid, Group group, Result<MembersRecord> input) {
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

    public ListenableFuture<SocietyMember> getMember(final UUID uuid, final Group group) {
        return prepare(uuid, new Function<Result<MembersRecord>, SocietyMember>() {
            @Nullable
            @Override
            public SocietyMember apply(@Nullable Result<MembersRecord> input) {
                return evaluate(uuid, group, input);
            }
        });
    }

    @Override
    public ListenableFuture<SocietyMember> getMember(String name) {
        Player player = playerProvider.getPlayer(name);

        if (player == null) {
            return Futures.immediateCheckedFuture(null);
        }

        return getMember(player.getUniqueId());
    }

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
                }

                SocietiesRecord record = Iterables.getOnlyElement(input);

                return evaluateSingle(record);
            }
        });

    }

    private Group evaluateSingle(SocietiesRecord record) {
        Group group = evaluateGroup(record);

        // Get members
        Select<Record1<byte[]>> query = queries.getQuery(SQLQueries.SELECT_SOCIETY_MEMBERS);

        query.bind(1, record.getUuid());

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
        Select<Record3<byte[], UShort, byte[]>> settingsQuery = queries.getQuery(SQLQueries.SELECT_SOCIETY_SETTINGS);
        settingsQuery.bind(1, record.getUuid());

        for (Record3<byte[], UShort, byte[]> settingRecord : settingsQuery.fetch()) {
            int settingID = settingRecord.value2().intValue();

            Setting setting = settingProvider.getSetting(settingID);

            if (setting == null) {
                //invalid setting
                continue;
            }

            byte[] uuid = settingRecord.value1();
            Target target;

            if (uuid == null) {
                target = group;
            } else {
                target = new SimpleTarget(UUIDGen.toUUID(uuid));
            }

            Object value = setting.convert(settingRecord.value3());

            group.set(setting, target, value);
        }

        //Load ranks
        Select<RanksRecord> rankQuery = queries.getQuery(SQLQueries.SELECT_GROUP_RANKS);
        rankQuery.bind(1, record.getUuid());

        for (RanksRecord ranksRecord : rankQuery.fetch()) {
            Rank rank = rankFactory.create(UUIDGen.toUUID(ranksRecord.getUuid()), ranksRecord.getName(), 0);
            group.addRank(rank);
        }

        return group;
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
                    groups.add(evaluateSingle(record));
                }

                return groups;
            }
        });
    }

    private Group evaluateGroup(SocietiesRecord record) {
        Group group = groupFactory.create(UUIDGen.toUUID(record.getUuid()), record.getName(), record.getTag());
        group.setState(record.getState());
        return group;
    }

    private Group createGroup(UUID uuid) {
        return groupFactory.create(uuid, Group.NEW_GROUP_NAME, Group.NEW_GROUP_TAG);
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
                Insert<SocietiesRecord> query = queries.getQuery(SQLQueries.INSERT_SOCIETY);

                query.bind(1, UUIDGen.toByteArray(group.getUUID()));

                query.bind(2, group.getName());
                query.bind(3, group.getTag());

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
                Query query = queries.getQuery(SQLQueries.DROP_SOCIETY_BY_UUID);

                query.bind(1, UUIDGen.toByteArray(group.getUUID()));

                query.execute();
            }
        });
    }
}
