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
import org.jooq.Result;
import org.jooq.Select;
import org.societies.api.PlayerResolver;
import org.societies.database.sql.layout.tables.records.MembersRecord;
import org.societies.database.sql.layout.tables.records.SocietiesRecord;
import org.societies.groups.cache.GroupCache;
import org.societies.groups.cache.MemberCache;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupFactory;
import org.societies.groups.group.GroupProvider;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberFactory;
import org.societies.groups.member.MemberProvider;
import org.societies.groups.member.MemberPublisher;
import org.societies.groups.rank.RankFactory;
import org.societies.groups.setting.SettingProvider;
import org.societies.member.MemberException;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

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

        return evaluateSingleMember(uuid);
    }

    private Member evaluateSingleMember(UUID uuid) {
        Member member = memberFactory.create(uuid);
        memberCache.cache(member);
        return member;
    }

    //================================================================================
    // Groups
    //================================================================================

    @Override
    public ListenableFuture<Group> getGroup(UUID uuid) {
        return getGroup(uuid, service);
    }

    public ListenableFuture<Group> getGroup(UUID uuid, ListeningExecutorService service) {
        // Cache lookup
        Group group = groupCache.getGroup(uuid);
        if (group != null) {
            return immediateFuture(group);
        }

        Select<SocietiesRecord> query = queries.getQuery(SELECT_SOCIETY_BY_UUID);
        query.bind(1, ByteUtil.toByteArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));

        return querySingleGroup(uuid, queries.query(service, query));
    }

    private ListenableFuture<Group> querySingleGroup(final UUID uuid, final ListenableFuture<Result<SocietiesRecord>> result) {
        return transform(result, new Function<Result<SocietiesRecord>, Group>() {

            @Nullable
            @Override
            public Group apply(@Nullable Result<SocietiesRecord> input) {
                if (input == null) {
                    return null;
                }

                if (input.isEmpty()) {
                    throw new RuntimeException("Group not found!");
                }

                SocietiesRecord record = Iterables.getOnlyElement(input);

                return evaluateSingleGroup(uuid, record);
            }
        }, service);
    }

    private Group evaluateSingleGroup(UUID uuid, SocietiesRecord record) {
        Group group = groupFactory.create(uuid, record.getName(), record.getTag(), new DateTime(record.getCreated()));
        groupCache.cache(group);
        return group;
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
                    UUID uuid = UUIDGen.toUUID(record.getUuid());
                    groups.add(evaluateSingleGroup(uuid, record));
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
