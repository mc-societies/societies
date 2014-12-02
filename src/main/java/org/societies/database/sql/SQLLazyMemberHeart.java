package org.societies.database.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import gnu.trove.set.hash.THashSet;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.societies.groups.event.EventController;
import org.societies.groups.group.GroupHeart;
import org.societies.groups.group.GroupProvider;
import org.societies.groups.member.Member;
import org.societies.groups.rank.Rank;

import java.util.Collections;
import java.util.Set;

/**
 * Represents a LazySQLMemberHearth
 */
class SQLLazyMemberHeart extends SQLMemberHeart {

    @Nullable
    private GroupHeart group;
    private Set<Rank> ranks = new THashSet<Rank>();
    private DateTime lastActive;
    private DateTime created;

    @Inject
    public SQLLazyMemberHeart(@Assisted Member member,
                              EventController events,
                              @Named("default-rank") Rank defaultRank,
                              GroupProvider groupProvider,
                              SQLQueries queries, ListeningExecutorService service) {
        super(member, events, defaultRank, groupProvider, queries, service);
    }

    @Override
    public void setGroup(@Nullable GroupHeart group) {
        this.group = group;

        super.setGroup(group);

        if (group != null && !group.isMember(member)) {
            group.addMember(member);
        }
    }

    @Override
    public void setLastActive(DateTime lastActive) {
        this.lastActive = lastActive;

        super.setLastActive(lastActive);
    }

    @Override
    public DateTime getCreated() {
        if (created != null) {
            return created;
        }

        return created = super.getCreated();
    }

    @Override
    public void setCreated(DateTime created) {
        this.created = created;

        super.setCreated(created);
    }

    @Nullable
    @Override
    public GroupHeart getGroup() {
        if (group != null) {
            return group;
        }
        return group = super.getGroup();
    }

    @Override
    public DateTime getLastActive() {
        if (lastActive != null) {
            return lastActive;
        }

        return lastActive = super.getLastActive();
    }

    @Override
    public void addRank(Rank rank) {
        if (getGroup() == null) {
            return;
        }

        if (ranks == null) {
            ranks = getRanks();
        }

        ranks.add(rank);

        super.addRank(rank);
    }

    @Override
    public boolean removeRank(Rank rank) {
        if (getGroup() == null) {
            return false;
        }

        if (ranks == null) {
            ranks = getRanks();
        }

        ranks.remove(rank);

        return super.removeRank(rank);
    }

    @Override
    public Set<Rank> getRanks() {
        if (getGroup() == null) {
            return Collections.emptySet();
        }

        if (ranks == null) {
            ranks = super.getRanks();
        }

        return ranks;
    }
}
