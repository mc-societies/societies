package net.catharos.societies.database.sql;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Inject;
import gnu.trove.set.hash.THashSet;
import net.catharos.groups.*;
import net.catharos.groups.publisher.MemberPublisher;
import net.catharos.groups.rank.Rank;
import net.catharos.groups.rank.RankFactory;
import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.SettingException;
import net.catharos.groups.setting.SettingProvider;
import net.catharos.groups.setting.subject.Subject;
import net.catharos.groups.setting.target.SimpleTarget;
import net.catharos.groups.setting.target.Target;
import net.catharos.lib.core.util.ByteUtil;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.societies.PlayerResolver;
import net.catharos.societies.database.sql.layout.tables.records.MembersRecord;
import net.catharos.societies.database.sql.layout.tables.records.SocietiesRecord;
import net.catharos.societies.group.SocietyException;
import net.catharos.societies.member.MemberException;
import net.catharos.societies.member.SocietyMember;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.Select;
import org.jooq.types.UShort;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.google.common.util.concurrent.Futures.immediateFuture;
import static com.google.common.util.concurrent.Futures.transform;
import static net.catharos.societies.database.sql.SQLQueries.*;

/**
 * Represents a LoadingMemberProvider
 */
class SQLProvider implements MemberProvider<SocietyMember>, GroupProvider {

    public static final int PREPARE = DefaultGroup.PREPARE;

    private final SQLQueries queries;
    private final ListeningExecutorService service;

    private final MemberPublisher memberPublisher;

    private final MemberFactory<SocietyMember> memberFactory;
    private final GroupFactory groupFactory;
    private final RankFactory rankFactory;

    private final PlayerResolver playerResolver;
    private final SettingProvider settingProvider;


    private final GroupCache groupCache;
    private final MemberCache<SocietyMember> memberCache;

    @InjectLogger
    private Logger logger;

    @Inject
    public SQLProvider(SQLQueries queries,
                       ListeningExecutorService service,
                       PlayerResolver playerResolver,
                       MemberFactory<SocietyMember> memberFactory,
                       MemberPublisher memberPublisher,
                       GroupCache groupCache,
                       GroupFactory groupFactory,
                       SettingProvider settingProvider,
                       RankFactory rankFactory,
                       MemberCache<SocietyMember> memberCache) {
        this.queries = queries;
        this.service = service;

        this.playerResolver = playerResolver;
        this.memberFactory = memberFactory;
        this.memberPublisher = memberPublisher;
        this.groupCache = groupCache;
        this.groupFactory = groupFactory;
        this.settingProvider = settingProvider;
        this.rankFactory = rankFactory;
        this.memberCache = memberCache;
    }

    //================================================================================
    // Members
    //================================================================================

    @Override
    public ListenableFuture<SocietyMember> getMember(String name) {
        // Cache lookup
        SocietyMember member = memberCache.getMember(name);
        if (member != null) {
            return immediateFuture(member);
        }


        UUID player = playerResolver.getPlayer(name);

        if (player == null) {
            return immediateFuture(null);
        }

        return getMember(player);
    }

    @Override
    public ListenableFuture<Set<SocietyMember>> getMembers() {
        Set<SocietyMember> members = memberCache.getMembers();
        if (members != null) {
            return immediateFuture(members);
        }

        ListenableFuture<Result<MembersRecord>> future = queries.query(service, SQLQueries.SELECT_MEMBERS);

        return transform(future, new Function<Result<MembersRecord>, Set<SocietyMember>>() {
            @Nullable
            @Override
            public Set<SocietyMember> apply(@Nullable Result<MembersRecord> input) {
                if (input == null) {
                    return Collections.emptySet();
                }

                THashSet<SocietyMember> result = new THashSet<SocietyMember>(input.size());

                for (MembersRecord record : input) {
                    result.add(evaluateSingleMember(UUIDGen.toUUID(record.getUuid()), null, record));
                }

                return result;
            }
        });
    }

    @Override
    public ListenableFuture<SocietyMember> getMember(UUID uuid) {
        return getMember(uuid, null, service);
    }

    public ListenableFuture<SocietyMember> getMember(final UUID uuid, final Group group, ListeningExecutorService service) {
        // Cache lookup
        SocietyMember member = memberCache.getMember(uuid);
        if (member != null) {
            return immediateFuture(member);
        }

        return queryMember(service, uuid, new Function<Result<MembersRecord>, SocietyMember>() {
            @Nullable
            @Override
            public SocietyMember apply(@Nullable Result<MembersRecord> input) {
                return evaluateMember(uuid, group, input);
            }
        });
    }

    private ListenableFuture<SocietyMember> queryMember(ListeningExecutorService service, UUID uuid, Function<Result<MembersRecord>, SocietyMember> applier) {
        Select<MembersRecord> query = queries.getQuery(SQLQueries.SELECT_MEMBER_BY_UUID);
        query.bind(1, ByteUtil.toByteArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));

        ListenableFuture<Result<MembersRecord>> future = queries.query(service, query);

        return transform(future, applier, service);
    }

    private SocietyMember evaluateMember(UUID uuid, Group predefined, Result<MembersRecord> input) {
        if (input == null) {
            return null;
        }

        if (input.isEmpty()) {
            SocietyMember created = memberFactory.create(uuid);
            memberPublisher.publish(created);
            return created;
        } else if (input.size() > 1) {
            throw new MemberException(uuid, "There are more users with the same uuid?!");
        }

        MembersRecord record = input.get(0);

        return evaluateSingleMember(uuid, predefined, record);
    }

    private SocietyMember evaluateSingleMember(UUID uuid, Group predefined, MembersRecord record) {
        byte[] byteUUID = record.getUuid();
        SocietyMember member = memberFactory.create(UUIDGen.toUUID(byteUUID));

        member.complete(false);

        try {
            // Load society
            byte[] rawSociety = record.getSociety();

            if (rawSociety != null && rawSociety.length == UUIDGen.UUID_LENGTH) {
                // Load group if necessary
                if (predefined == null) {
                    try {
                        predefined = getGroup(UUIDGen.toUUID(rawSociety), member, MoreExecutors.sameThreadExecutor())
                                .get();
                    } catch (InterruptedException e) {
                        throw new MemberException(uuid, e, "Failed to set group of member!");
                    } catch (ExecutionException e) {
                        throw new MemberException(uuid, e, "Failed to set group of member!");
                    }
                }

                // Load ranks for member (get by group)
                if (predefined != null) {
                    member.setGroup(predefined);

                    //Load ranks
                    Select<Record1<byte[]>> query = queries.getQuery(SQLQueries.SELECT_MEMBER_RANKS);
                    query.bind(1, byteUUID);

                    for (Record1<byte[]> rankRecord : query.fetch()) {
                        UUID rankUUID = UUIDGen.toUUID(rankRecord.value1());
                        Rank rank = predefined.getRank(rankUUID);

                        if (rank != null) {
                            member.addRank(rank);
                        }
                    }
                }

                //Load settings
                loadSettings(member, byteUUID, queries.getQuery(SQLQueries.SELECT_SOCIETY_SETTINGS));
            }

        } finally {
            // Finished
            member.complete();
        }

        memberCache.cache(member);

        return member;
    }

    //================================================================================
    // Drop
    //================================================================================

//    @Override
//    public ListenableFuture<?> drop(final SocietyMember member) {
//        return service.submit(new Runnable() {
//            @Override
//            public void run() {
//                Query query = queries.getQuery(SQLQueries.DROP_MEMBER_BY_UUID);
//
//                query.bind(1, UUIDGen.toByteArray(member.getUUID()));
//
//                query.execute();
//            }
//        });
//    }


    //================================================================================
    // Groups
    //================================================================================

    @Override
    public ListenableFuture<Group> getGroup(UUID uuid) {
        return getGroup(uuid, null, service);
    }

    public ListenableFuture<Group> getGroup(UUID uuid, Member predefined, ListeningExecutorService service) {
        // Cache lookup
        Group group = groupCache.getGroup(uuid);
        if (group != null) {
            return immediateFuture(group);
        }

        Select<SocietiesRecord> query = queries.getQuery(SELECT_SOCIETY_BY_UUID);
        query.bind(1, ByteUtil.toByteArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));

        return querySingleGroup(uuid, queries.query(service, query), predefined);
    }

    private ListenableFuture<Group> querySingleGroup(final UUID uuid, final ListenableFuture<Result<SocietiesRecord>> result, final Member predefined) {
        return transform(result, new Function<Result<SocietiesRecord>, Group>() {

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
        }, service);
    }

    private Group evaluateSingleGroup(SocietiesRecord record, Member predefined) {
        byte[] uuid = record.getUuid();
        Group group = groupFactory
                .create(UUIDGen.toUUID(uuid), record.getName(), record.getTag(), new DateTime(record.getCreated()));

        group.complete(false);

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
                        memberToAdd = getMember(memberUUID, group, MoreExecutors.sameThreadExecutor()).get();
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
            Select<Record3<byte[], String, Short>> rankQuery = queries.getQuery(SQLQueries.SELECT_GROUP_RANKS);
            rankQuery.bind(1, uuid);


            for (Record3<byte[], String, Short> rankRecord : rankQuery.fetch()) {
                Rank rank = loadRank(group, rankRecord);
                group.addRank(rank);
            }

        } finally {
            // Finished
            group.complete();
        }

        groupCache.cache(group);

        return group;
    }

    private Rank loadRank(Group group, Record3<byte[], String, Short> rankRecord) {
        Rank rank = rankFactory
                .create(UUIDGen.toUUID(rankRecord.value1()), rankRecord.value2(), rankRecord.value3(), group);
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
                logger.warn("Failed to convert setting %s!", settingID);
                continue;
            }

            byte[] targetUUID = settingRecord.value1();
            Target target;

            if (targetUUID == null) {
                target = subject;
            } else {
                target = new SimpleTarget(UUIDGen.toUUID(targetUUID));
            }

            Object value;
            try {
                value = setting.convert(subject, target, settingRecord.value3());
            } catch (SettingException e) {
                continue;
            }

            subject.set(setting, target, value);
        }
    }

    @Override
    public ListenableFuture<Set<Group>> getGroup(String tag) {
        // Cache lookup
        Set<Group> group = groupCache.getGroup(tag);
        if (group != null) {
            return immediateFuture(group);
        }

        Select<SocietiesRecord> query = queries.getQuery(SELECT_SOCIETY_BY_TAG);
        query.bind(1, tag);

        return evaluateMultipleGroups(queries.query(service, query));
    }

    private ListenableFuture<Set<Group>> evaluateMultipleGroups(ListenableFuture<Result<SocietiesRecord>> result) {
        return transform(result, new Function<Result<SocietiesRecord>, Set<Group>>() {

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
    // Drop
    //================================================================================

//    @Override
//    public ListenableFuture<?> drop(final Group group) {
//        return service.submit(new Runnable() {
//            @Override
//            public void run() {
//                Query query = queries.getQuery(SQLQueries.DROP_SOCIETY_BY_UUID);
//
//                query.bind(1, UUIDGen.toByteArray(group.getUUID()));
//
//                query.execute();
//            }
//        });
//    }
}
