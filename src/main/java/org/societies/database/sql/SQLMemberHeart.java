package org.societies.database.sql;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import gnu.trove.set.hash.THashSet;
import net.catharos.lib.core.uuid.UUIDGen;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.jooq.*;
import org.societies.api.member.MemberException;
import org.societies.database.sql.layout.tables.records.MembersRecord;
import org.societies.database.sql.layout.tables.records.RanksRecord;
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

import static net.catharos.lib.core.uuid.UUIDGen.*;

/**
 * Represents a SQLMember
 */
public class SQLMemberHeart extends AbstractMemberHeart implements MemberHeart {

    protected final Member member;

    private final Rank defaultRank;
    private final GroupProvider groupProvider;

    private final Queries queries;
    private final ListeningExecutorService service;

    @Inject
    public SQLMemberHeart(@Assisted Member member,
                          EventController events,
                          @Named("default-rank") Rank defaultRank,
                          GroupProvider groupProvider,
                          Queries queries, ListeningExecutorService service) {
        super(events);
        this.defaultRank = defaultRank;
        this.groupProvider = groupProvider;
        this.queries = queries;
        this.service = service;
        this.member = member;
    }

    private byte[] getByteUUID() {
        return toByteArray(member.getUUID());
    }

    @Override
    public Set<Rank> getRanks() {
        GroupHeart group = getGroup();

        if (group == null) {
            return Collections.emptySet();
        }

        THashSet<Rank> ranks = new THashSet<Rank>();

        Select<Record1<byte[]>> query = queries.getQuery(Queries.SELECT_MEMBER_RANKS);
        query.bind(1, getByteUUID());

        for (Record1<byte[]> rankRecord : query.fetch()) {
            UUID rankUUID = toUUID(rankRecord.value1());
            Rank rank = group.getRank(rankUUID);

            if (rank != null) {
                ranks.add(rank);
            }
        }

        return Sets.union(ranks, Collections.singleton(defaultRank));
    }

    @Override
    public void addRank(final Rank rank) {
        if (getGroup() == null) {
            return;
        }

        //beautify duplicate
        service.submit(new Callable<Rank>() {
            @Override
            public Rank call() throws Exception {
                byte[] uuid = UUIDGen.toByteArray(rank.getUUID());
                String name = rank.getName();
                int priority = rank.getPriority();

                Insert<RanksRecord> query = queries.getQuery(Queries.INSERT_RANK);

                query.bind(1, uuid);
                query.bind(2, name);
                query.bind(3, priority);
                query.bind(4, uuid);
                query.bind(5, name);
                query.bind(6, priority);
                query.execute();
                return rank;
            }
        });

        service.submit(new Callable<Member>() {
            @Override
            public Member call() throws Exception {
                Insert query = queries.getQuery(Queries.INSERT_MEMBER_RANK);
                query.bind(1, getByteUUID());
                query.bind(2, toByteArray(rank.getUUID()));
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
                query.bind(1, getByteUUID());
                query.bind(2, toByteArray(rank.getUUID()));
                query.execute();
                return member;
            }
        });
        return true;
    }


    @Override
    public DateTime getLastActive() {
        Select<Record1<Timestamp>> query = queries.getQuery(Queries.SELECT_MEMBER_LAST_ACTIVE);
        query.bind(1, getByteUUID());

        Record1<Timestamp> record = query.fetch().get(0);
        return new DateTime(record.value1());
    }

    @Override
    public void setLastActive(final DateTime lastActive) {
        service.submit(new Callable<Member>() {
            @Override
            public Member call() throws Exception {
                Update<MembersRecord> query = queries.getQuery(Queries.UPDATE_MEMBER_LAST_ACTIVE);

                query.bind(1, getByteUUID());
                query.bind(2, new Timestamp(lastActive.getMillis()));

                query.execute();
                return member;
            }
        });
    }

    @Override
    public DateTime getCreated() {
        Select<Record1<Timestamp>> query = queries.getQuery(Queries.SELECT_MEMBER_CREATED);
        query.bind(1, getByteUUID());

        Record1<Timestamp> record = query.fetch().get(0);
        return new DateTime(record.value1());
    }

    @Override
    public void setCreated(final DateTime created) {
        service.submit(new Callable<Member>() {
            @Override
            public Member call() throws Exception {
                Update<MembersRecord> query = queries.getQuery(Queries.UPDATE_MEMBER_CREATED);

                query.bind(1, getByteUUID());
                query.bind(2, new Timestamp(created.getMillis()));

                query.execute();
                return member;
            }
        });
    }

    @Override
    @Nullable
    public GroupHeart getGroup() {
        Select<Record1<byte[]>> query = queries.getQuery(Queries.SELECT_MEMBER_SOCIETY);
        query.bind(1, getByteUUID());

        Result<Record1<byte[]>> result = query.fetch();

        if (result.isEmpty()) {
            return null;
        }

        Record1<byte[]> record = result.get(0);

        Group group = null;
        byte[] rawSociety = record.value1();

        if (rawSociety != null && rawSociety.length == UUID_LENGTH) {
            try {
                group = groupProvider.getGroup(toUUID(rawSociety)).get();
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

                query.bind(1, toByteArray(uuid));
                query.bind(2, getByteUUID());

                query.execute();
                return member;
            }
        });


        publishMemberEvents(member, group, previous);
    }
}
