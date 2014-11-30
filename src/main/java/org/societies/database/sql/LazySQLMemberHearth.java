package org.societies.database.sql;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import gnu.trove.set.hash.THashSet;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupHeart;
import org.societies.groups.group.GroupProvider;
import org.societies.groups.member.DefaultMemberHeart;
import org.societies.groups.member.Member;
import org.societies.groups.rank.Rank;

import java.util.Set;

/**
 * Represents a LazySQLMemberHearth
 */
class LazySQLMemberHearth extends SQLMemberHearth {

    @Nullable
    private GroupHeart group;
    private THashSet<Rank> ranks = new THashSet<Rank>();
    private DateTime lastActive;
    private DateTime created;

    @Inject
    public LazySQLMemberHearth(DefaultMemberHeart.Statics statics, GroupProvider groupProvider, @Assisted Member member, SQLQueries queries) {
        super(statics, groupProvider, member, queries);
    }

    @Override
    public void setGroup(@Nullable GroupHeart group) {

        super.setGroup(group);

        this.group = group;
    }

    @Override
    public void setLastActive(DateTime lastActive) {
        super.setLastActive(lastActive);
        this.lastActive = lastActive;
    }

    @Override
    public DateTime getCreated() {
        return super.getCreated();
    }

    @Override
    public void setCreated(DateTime created) {
        super.setCreated(created);
    }

    @Nullable
    @Override
    public Group getGroup() {
        return super.getGroup();
    }

    @Override
    public DateTime getLastActive() {
        return super.getLastActive();
    }

    @Override
    public void addRank(Rank rank) {
        super.addRank(rank);
    }

    @Override
    public boolean removeRank(Rank rank) {
        return super.removeRank(rank);
    }

    @Override
    public Set<Rank> getRanks() {
        return super.getRanks();
    }
}
