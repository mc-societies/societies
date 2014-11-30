package org.societies.database.sql;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import gnu.trove.set.hash.THashSet;
import net.catharos.lib.core.uuid.UUIDGen;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.Select;
import org.societies.groups.event.EventController;
import org.societies.groups.event.MemberJoinEvent;
import org.societies.groups.event.MemberLeaveEvent;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupHeart;
import org.societies.groups.group.GroupProvider;
import org.societies.groups.member.AbstractMemberHeart;
import org.societies.groups.member.DefaultMemberHeart;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberHeart;
import org.societies.groups.publisher.MemberCreatedPublisher;
import org.societies.groups.publisher.MemberGroupPublisher;
import org.societies.groups.publisher.MemberLastActivePublisher;
import org.societies.groups.publisher.MemberRankPublisher;
import org.societies.groups.rank.Rank;
import org.societies.member.MemberException;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Represents a SQLMember
 */
class SQLMemberHearth extends AbstractMemberHeart implements MemberHeart {
    protected final Statics statics;
    private final Member member;

    @Inject
    public SQLMemberHearth(Statics statics, @Assisted Member member) {
        this.statics = statics;
        this.member = member;
    }

    @Override
    public Member getHolder() {
        return member;
    }

    @Override
    public Set<Rank> getRanks() {
        GroupHeart group = getGroup();

        if (group == null) {
            return Collections.emptySet();
        }

        THashSet<Rank> ranks = new THashSet<Rank>();

        Select<Record1<byte[]>> query = statics.queries.getQuery(SQLQueries.SELECT_MEMBER_RANKS);
        query.bind(1, UUIDGen.toByteArray(member.getUUID()));

        for (Record1<byte[]> rankRecord : query.fetch()) {
            UUID rankUUID = UUIDGen.toUUID(rankRecord.value1());
            Rank rank = group.getRank(rankUUID);

            if (rank != null) {
                member.addRank(rank);
            }
        }

        return ranks;
    }

    @Override
    public void addRank(Rank rank) {
        if (getGroup() == null) {
            return;
        }

        if (member.isCompleted()) {
            statics.publishRank(member, rank);
        }
    }

    @Override
    public boolean removeRank(Rank rank) {
        if (getGroup() == null) {
            return false;
        }

        if (member.isCompleted()) {
            statics.dropRank(member, rank);
            return true;
        }

        return false;
    }


    @Override
    public DateTime getLastActive() {
        Select<Record1<Timestamp>> query = statics.queries.getQuery(SQLQueries.SELECT_MEMBER_LAST_ACTIVE);
        query.bind(1, UUIDGen.toByteArray(member.getUUID()));

        Record1<Timestamp> record = query.fetch().get(0);
        return new DateTime(record.value1());
    }

    @Override
    public void setLastActive(DateTime lastActive) {
        if (member.isCompleted()) {
            statics.publishLastActive(member, lastActive);
        }
    }

    @Override
    public DateTime getCreated() {
        Select<Record1<Timestamp>> query = statics.queries.getQuery(SQLQueries.SELECT_MEMBER_CREATED);
        query.bind(1, UUIDGen.toByteArray(member.getUUID()));

        Record1<Timestamp> record = query.fetch().get(0);
        return new DateTime(record.value1());
    }

    @Override
    public void setCreated(DateTime created) {
        if (member.isCompleted()) {
            statics.publishCreated(member, created);
        }
    }

    @Override
    @Nullable
    public GroupHeart getGroup() {
        Select<Record1<byte[]>> query =  statics.queries.getQuery(SQLQueries.SELECT_MEMBER_SOCIETY);
        query.bind(1, UUIDGen.toByteArray(member.getUUID()));

        Result<Record1<byte[]>> result = query.fetch();

        if (result.isEmpty()) {
            return null;
        }

        Record1<byte[]> record = result.get(0);

        Group group = null;
        byte[] rawSociety = record.value1();

        if (rawSociety != null && rawSociety.length == UUIDGen.UUID_LENGTH) {

            try {
                group = statics.groupProvider.getGroup(UUIDGen.toUUID(rawSociety)).get();
            } catch (InterruptedException e) {
                throw new MemberException(member.getUUID(), e, "Failed to set group of member!");
            } catch (ExecutionException e) {
                throw new MemberException(member.getUUID(), e, "Failed to set group of member!");
            }
        }

        return group;
    }

    @Override
    public void setGroup(@Nullable GroupHeart group) {
//        GroupHeart previous = getGroup();  fixme
//        if (Objects.equal(previous, group)) {
//            return;                         fixme
//        }           fixme

        if (member.isCompleted()) {
            statics.publishGroup(member, group == null ? null : group.getHolder());
        }

        if (group == null) {
            //fixme clear ranks in database
            statics.publish(new MemberLeaveEvent(member, group == null ? null : group.getHolder()));
        } else {
            statics.publish(new MemberJoinEvent(member));
        }

        if (group != null && !group.isMember(member)) {
            group.addMember(member);
        }
    }

    @Singleton
    public static class Statics extends DefaultMemberHeart.Statics {

        private final GroupProvider groupProvider;
        private final SQLQueries queries;

        @Inject
        public Statics(MemberGroupPublisher groupPublisher,
                       MemberRankPublisher memberRankPublisher,
                       MemberLastActivePublisher lastActivePublisher,
                       MemberCreatedPublisher createdPublisher,
                       EventController eventController,
                       @Named("default-rank") Rank defaultRank,
                       GroupProvider groupProvider, SQLQueries queries) {
            super(groupPublisher, memberRankPublisher, lastActivePublisher, createdPublisher, eventController, defaultRank);
            this.groupProvider = groupProvider;
            this.queries = queries;
        }

        public GroupProvider getGroupProvider() {
            return groupProvider;
        }

        public SQLQueries getQueries() {
            return queries;
        }
    }
}
