package org.societies.sql;

import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import gnu.trove.set.hash.THashSet;
import net.catharos.lib.core.util.ByteUtil;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.Select;
import org.shank.logging.InjectLogger;
import org.societies.api.PlayerResolver;
import org.societies.api.member.MemberException;
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

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.societies.sql.Queries.*;

/**
 * Represents a LoadingMemberProvider
 */
class SQLProvider implements MemberProvider, GroupProvider {

    private final Queries queries;
    private final ListeningExecutorService service;

    private final MemberPublisher memberPublisher;

    private final MemberFactory memberFactory;
    private final GroupFactory groupFactory;

    private final PlayerResolver playerResolver;

    private final GroupCache groupCache;
    private final MemberCache memberCache;

    @InjectLogger
    private Logger logger;

    @Inject
    public SQLProvider(Queries queries,
                       ListeningExecutorService service,
                       PlayerResolver playerResolver,
                       MemberFactory memberFactory,
                       MemberPublisher memberPublisher,
                       GroupCache groupCache,
                       GroupFactory groupFactory,
                       MemberCache memberCache) {
        this.queries = queries;
        this.service = service;

        this.playerResolver = playerResolver;
        this.memberFactory = memberFactory;
        this.memberPublisher = memberPublisher;
        this.groupCache = groupCache;
        this.groupFactory = groupFactory;
        this.memberCache = memberCache;
    }

    //================================================================================
    // Members
    //================================================================================

    @Override
    public Member getMember(String name) {
        // Cache lookup
        Member member = memberCache.getMember(name);
        if (member != null) {
            return member;
        }

        UUID player = playerResolver.getPlayer(name);

        if (player == null) {
            return null;
        }

        return getMember(player);
    }

    @Override
    public Set<Member> getMembers() {
        Set<Member> members = memberCache.getMembers();
        if (members != null) {
            return members;
        }

        Result<MembersRecord> result = queries.query(SELECT_MEMBERS);

        if (result == null) {
            return Collections.emptySet();
        }

        THashSet<Member> ret = new THashSet<Member>(result.size());

        for (MembersRecord record : result) {
            ret.add(memberFactory.create(record.getUuid()));
        }

        return ret;
    }


    @Override
    public Member getMember(final UUID uuid) {
        // Cache lookup
        Member member = memberCache.getMember(uuid);
        if (member != null) {
            return member;
        }

        Result<MembersRecord> result = queryMember(uuid);

        return evaluateMember(uuid, result);
    }

    private Result<MembersRecord> queryMember(UUID uuid) {
        Select<MembersRecord> query = queries.getQuery(SELECT_MEMBER_BY_UUID);
        query.bind(1, ByteUtil.toByteArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));

        return queries.query(query);
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
    public Group getGroup(UUID uuid) {
        return getGroup(uuid, service);
    }

    public Group getGroup(UUID uuid, ListeningExecutorService service) {
        // Cache lookup
        Group group = groupCache.getGroup(uuid);
        if (group != null) {
            return group;
        }

        Select<SocietiesRecord> query = queries.getQuery(SELECT_SOCIETY_BY_UUID);
        query.bind(1, ByteUtil.toByteArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));

        return querySingleGroup(uuid, queries.query(query));
    }

    private Group querySingleGroup(final UUID uuid, Result<SocietiesRecord> result) {
        if (result.isEmpty()) {
            throw new RuntimeException("Group not found!");
        }

        SocietiesRecord record = Iterables.getOnlyElement(result);

        return evaluateSingleGroup(uuid, record);
    }

    private Group evaluateSingleGroup(UUID uuid, SocietiesRecord record) {
        Group group = groupFactory.create(uuid, record.getName(), record.getTag(), new DateTime(record.getCreated()));
        groupCache.cache(group);
        return group;
    }

    @Override
    public Set<Group> getGroup(String tag) {
        // Cache lookup
        Set<Group> group = groupCache.getGroup(tag);
        if (group != null) {
            return group;
        }

        Select<SocietiesRecord> query = queries.getQuery(SELECT_SOCIETY_BY_TAG);
        query.bind(1, tag);

        return evaluateMultipleGroups(queries.query(query));
    }

    private Set<Group> evaluateMultipleGroups(Result<SocietiesRecord> result) {
        THashSet<Group> groups = new THashSet<Group>(result.size());

        for (SocietiesRecord record : result) {
            UUID uuid = record.getUuid();
            groups.add(evaluateSingleGroup(uuid, record));
        }

        return groups;
    }

    @Override
    public Set<Group> getGroups() {
        return evaluateMultipleGroups(queries.query(SELECT_SOCIETIES));
    }

    @Override
    public Integer size() {
        Result<Record1<Integer>> result = queries.query(SELECT_SOCIETIES_AMOUNT);
        return Iterables.getOnlyElement(result).value1();
    }
}
