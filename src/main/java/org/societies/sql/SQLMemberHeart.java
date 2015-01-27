package org.societies.sql;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import gnu.trove.set.hash.THashSet;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.jooq.*;
import org.societies.api.member.MemberException;
import org.societies.database.QueryProvider;
import org.societies.database.sql.layout.tables.records.MembersRecord;
import org.societies.groups.event.EventController;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupHeart;
import org.societies.groups.group.GroupProvider;
import org.societies.groups.member.AbstractMemberHeart;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberHeart;
import org.societies.groups.rank.Rank;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Represents a SQLMember
 */
public class SQLMemberHeart extends AbstractMemberHeart implements MemberHeart {

    protected final Member member;

    private final Rank defaultRank;
    private final GroupProvider groupProvider;

    private final QueryProvider queries;
    private final ListeningExecutorService service;

    @Inject
    public SQLMemberHeart(@Assisted Member member,
                          EventController events,
                          @Named("default-rank") Rank defaultRank,
                          GroupProvider groupProvider,
                          @Named("main") QueryProvider queries, ListeningExecutorService service) {
        super(events);
        this.defaultRank = defaultRank;
        this.groupProvider = groupProvider;
        this.queries = queries;
        this.service = service;
        this.member = member;
    }

    public UUID getUUID() {
        return member.getUUID();
    }

    @Override
    public Set<Rank> getRanks() {
        GroupHeart group = getGroup();

        if (group == null) {
            return Collections.emptySet();
        }

        THashSet<Rank> ranks = new THashSet<Rank>();

        Select<Record1<UUID>> query = queries.getQuery(Queries.SELECT_MEMBER_RANKS);
        query.bind(1, member.getUUID());

        for (Record1<UUID> rankRecord : query.fetch()) {
            UUID rankUUID = rankRecord.value1();
            Rank rank = group.getRank(rankUUID);

            if (rank != null) {
                ranks.add(rank);
            }
        }

        return Sets.union(Collections.singleton(defaultRank), ranks);
    }

    @Override
    public void addRank(final Rank rank) {
        if (getGroup() == null) {
            return;
        }

        service.submit(new Callable<Member>() {
            @Override
            public Member call() throws Exception {
                //beautify duplicate
                UUID uuid = rank.getUUID();
                String name = rank.getName();
                int priority = rank.getPriority();

                Insert<?> query = queries.getQuery(Queries.INSERT_RANK);

                query.bind(1, uuid);
                query.bind(2, name);
                query.bind(3, priority);
                query.bind(4, uuid);
                query.bind(5, name);
                query.bind(6, priority);
                query.execute();

                query = queries.getQuery(Queries.INSERT_MEMBER_RANK);
                query.bind(1, getUUID());
                query.bind(2, rank.getUUID());
                query.execute();
                return member;
            }
        });

    }

    @Override
    public boolean removeRank(final Rank rank) {
        if (getGroup() == null) {
            return false;
        }

        service.submit(new Callable<Member>() {
            @Override
            public Member call() throws Exception {
                Query query;
                query = queries.getQuery(Queries.DROP_MEMBER_RANK);
                query.bind(1, getUUID());
                query.bind(2, rank.getUUID());
                query.execute();
                return member;
            }
        });
        return true;
    }


    @Override
    public DateTime getLastActive() {
        Select<Record1<DateTime>> query = queries.getQuery(Queries.SELECT_MEMBER_LAST_ACTIVE);
        query.bind(1, getUUID());

        Record1<DateTime> record = query.fetch().get(0);
        return record.value1();
    }

    @Override
    public void setLastActive(final DateTime lastActive) {
        service.submit(new Callable<Member>() {
            @Override
            public Member call() throws Exception {
                Update<MembersRecord> query = queries.getQuery(Queries.UPDATE_MEMBER_LAST_ACTIVE);

                query.bind(1, getUUID());
                query.bind(2, new Timestamp(lastActive.getMillis()));

                query.execute();
                return member;
            }
        });
    }

    @Override
    public DateTime getCreated() {
        Select<Record1<DateTime>> query = queries.getQuery(Queries.SELECT_MEMBER_CREATED);
        query.bind(1, getUUID());

        Record1<DateTime> record = query.fetch().get(0);
        return record.value1();
    }

    @Override
    public void setCreated(final DateTime created) {
        service.submit(new Callable<Member>() {
            @Override
            public Member call() throws Exception {
                Update<MembersRecord> query = queries.getQuery(Queries.UPDATE_MEMBER_CREATED);

                query.bind(1, getUUID());
                query.bind(2, new Timestamp(created.getMillis()));

                query.execute();
                return member;
            }
        });
    }

    @Override
    @Nullable
    public GroupHeart getGroup() {
        Select<Record1<UUID>> query = queries.getQuery(Queries.SELECT_MEMBER_SOCIETY);
        query.bind(1, getUUID());

        Result<Record1<UUID>> result = query.fetch();

        if (result.isEmpty()) {
            return null;
        }

        Record1<UUID> record = result.get(0);

        Group group = null;
        UUID rawSociety = record.value1();

        if (rawSociety != null) {
            try {
                group = groupProvider.getGroup(rawSociety).get();
            } catch (InterruptedException e) {
                throw new MemberException(member.getUUID(), e, "Failed to set group of member!");
            } catch (ExecutionException e) {
                throw new MemberException(member.getUUID(), e, "Failed to set group of member!");
            }
        }

        return group;
    }

    @Override
    public void setGroup(@Nullable final GroupHeart group) {
        GroupHeart previous = getGroup();
        if (Objects.equal(previous, group)) {
            return;
        }

        service.submit(new Callable<Member>() {
            @Override
            public Member call() throws Exception {
                Update<MembersRecord> query = queries.getQuery(Queries.UPDATE_MEMBER_SOCIETY);

                UUID uuid = null;

                if (group != null) {
                    uuid = group.getUUID();
                }

                query.bind(1, uuid);
                query.bind(2, getUUID());

                query.execute();
                return member;
            }
        });


        publishMemberEvents(member, group, previous);
    }
}
