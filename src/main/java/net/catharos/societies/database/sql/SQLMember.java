package net.catharos.societies.database.sql;

import net.catharos.groups.Group;
import net.catharos.groups.MemoryMember;
import net.catharos.groups.rank.Rank;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a SQLMember
 */
public abstract class SQLMember extends MemoryMember {

    public SQLMember(UUID uuid) {
        super(uuid);
    }

    @Override
    public Set<Rank> getRanks() {
        return null;
    }

    @Override
    public void addRank(Rank rank) {

    }

    @Override
    public boolean removeRank(Rank rank) {
        return false;
    }

    @Override
    public DateTime getLastActive() {
        return null;
    }

    @Override
    public void setLastActive(DateTime lastActive) {

    }

    @Override
    public DateTime getCreated() {
        return null;
    }

    @Override
    public void setCreated(DateTime created) {

    }

    @Nullable
    @Override
    public Group getGroup() {
        return null;
    }

    @Override
    public boolean hasGroup() {
        return false;
    }

    @Override
    public void setGroup(@Nullable Group group) {

    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public boolean isGroup(Group group) {
        return false;
    }
}
