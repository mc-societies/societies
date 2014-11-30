package org.societies.database.sql;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import gnu.trove.set.hash.THashSet;
import net.catharos.lib.core.util.ByteUtil;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.lib.shank.logging.InjectLogger;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.Select;
import org.jooq.types.UShort;
import org.societies.api.PlayerResolver;
import org.societies.database.sql.layout.tables.records.MembersRecord;
import org.societies.database.sql.layout.tables.records.SocietiesRecord;
import org.societies.group.SocietyException;
import org.societies.groups.cache.GroupCache;
import org.societies.groups.cache.MemberCache;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupFactory;
import org.societies.groups.group.GroupProvider;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberFactory;
import org.societies.groups.member.MemberProvider;
import org.societies.groups.publisher.MemberPublisher;
import org.societies.groups.rank.Rank;
import org.societies.groups.rank.RankFactory;
import org.societies.groups.setting.Setting;
import org.societies.groups.setting.SettingException;
import org.societies.groups.setting.SettingProvider;
import org.societies.groups.setting.subject.Subject;
import org.societies.groups.setting.target.SimpleTarget;
import org.societies.groups.setting.target.Target;
import org.societies.member.MemberException;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.google.common.util.concurrent.Futures.immediateFuture;
import static com.google.common.util.concurrent.Futures.transform;
import static org.societies.database.sql.SQLQueries.*;

/**
 * Represents a LoadingMemberProvider
 */
class SQLProvider implements MemberProvider, GroupProvider {

    private final SQLQueries queries;
    private final ListeningExecutorService service;

    private final MemberPublisher memberPublisher;

    private final MemberFactory memberFactory;
    private final GroupFactory groupFactory;
    private final RankFactory rankFactory;

    private final PlayerResolver playerResolver;
    private final SettingProvider settingProvider;


    private final GroupCache groupCache;
    private final MemberCache memberCache;

    @InjectLogger
    private Logger logger;

    @Inject
    public SQLProvider(SQLQueries queries,
                       ListeningExecutorService service,
                       PlayerResolver playerResolver,
                       MemberFactory memberFactory,
                       MemberPublisher memberPublisher,
                       GroupCache groupCache,
                       GroupFactory groupFactory,
                       SettingProvider settingProvider,
                       RankFactory rankFactory,
                       MemberCache memberCache) {
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
    public ListenableFuture<Member> getMember(String name) {
        // Cache lookup
        Member member = memberCache.getMember(name);
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
    public ListenableFuture<Set<Member>> getMembers() {
        Set<Member> members = memberCache.getMembers();
        if (members != null) {
            return immediateFuture(members);
        }

        ListenableFuture<Result<MembersRecord>> future = queries.query(service, SELECT_MEMBERS);

        return transform(future, new Function<Result<MembersRecord>, Set<Member>>() {
            @Nullable
            @Override
            public Set<Member> apply(@Nullable Result<MembersRecord> input) {
                if (input == null) {
                    return Collections.emptySet();
                }

                THashSet<Member> result = new THashSet<Member>(input.size());

                for (MembersRecord record : input) {
                    result.add(memberFactory.create(UUIDGen.toUUID(record.getUuid())));
                }

                return result;
            }
        });
    }

    @Override
    public ListenableFuture<Member> getMember(UUID uuid) {
        return getMember(uuid, service);
    }

    public ListenableFuture<Member> getMember(final UUID uuid, ListeningExecutorService service) {
        // Cache lookup
        Member member = memberCache.getMember(uuid);
        if (member != null) {
            return immediateFuture(member);
        }

        return queryMember(service, uuid, new Function<Result<MembersRecord>, Member>() {
            @Nullable
            @Override
            public Member apply(@Nullable Result<MembersRecord> input) {
                return evaluateMember(uuid, input);
            }
        });
    }

    private ListenableFuture<Member> queryMember(ListeningExecutorService service, UUID uuid, Function<Result<MembersRecord>, Member> applier) {
        Select<MembersRecord> query = queries.getQuery(SELECT_MEMBER_BY_UUID);
        query.bind(1, ByteUtil.toByteArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));

        ListenableFuture<Result<MembersRecord>> future = queries.query(service, query);

        return transform(future, applier, service);
    }

    private Member evaluateMember(UUID uuid, Result<MembersRecord> input) {
        if (input == null) {
            return null;
        }

        if (input.isEmpty()) {
            Member created = memberFactory.create(uuid);
            memberPublisher.publish(created);
            return created;
        } else if (input.size() > 1) {
            throw new MemberException(uuid, "There are more users with the same uuid?!");
        }

        return memberFactory.create(uuid);
    }

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
            groupCache.cache(group);

            // Load members
            Select<Record1<byte[]>> query = queries.getQuery(SELECT_SOCIETY_MEMBERS);

            query.bind(1, uuid);

            for (Record1<byte[]> member : query.fetch()) {
                try {
                    UUID memberUUID = UUIDGen.toUUID(member.value1());
                    Member memberToAdd;

                    if (predefined != null && predefined.getUUID().equals(memberUUID)) {
                        memberToAdd = predefined;
                    } else {
                        memberToAdd = getMember(memberUUID).get();
                    }

                    group.addMember(memberToAdd);
                } catch (InterruptedException e) {
                    throw new SocietyException(e, "Failed to add member to group!");
                } catch (ExecutionException e) {
                    throw new SocietyException(e, "Failed to add member to group!");
                }
            }

            //Load settings
            loadSettings(group, uuid, queries.getQuery(SELECT_SOCIETY_SETTINGS));

            //Load ranks
            Select<Record3<byte[], String, Short>> rankQuery = queries.getQuery(SELECT_SOCIETY_RANKS);
            rankQuery.bind(1, uuid);


            for (Record3<byte[], String, Short> rankRecord : rankQuery.fetch()) {
                Rank rank = loadRank(group, rankRecord);
                group.addRank(rank);
            }

        } catch (RuntimeException e) {
            groupCache.clear(group);
            throw e;
        } finally {
            // Finished
            group.complete();
        }

        return group;
    }

    private Rank loadRank(Group group, Record3<byte[], String, Short> rankRecord) {
        Rank rank = rankFactory
                .create(UUIDGen.toUUID(rankRecord.value1()), rankRecord.value2(), rankRecord.value3(), group);
        rank.complete(false);

        loadSettings(rank, rankRecord.value1(), queries.getQuery(SELECT_RANK_SETTINGS));

        rank.complete();
        return rank;
    }

    public void loadSettings(Subject subject, byte[] uuid, Select<Record3<byte[], UShort, byte[]>> query) {
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

    @Override
    public ListenableFuture<Integer> size() {
        return transform(queries
                .query(service, SELECT_SOCIETIES_AMOUNT), new Function<Result<Record1<Integer>>, Integer>() {

            @Nullable
            @Override
            public Integer apply(@Nullable Result<Record1<Integer>> input) {
                if (input == null) {
                    return -1;
                }

                return Iterables.getOnlyElement(input).value1();
            }
        });
    }
}
