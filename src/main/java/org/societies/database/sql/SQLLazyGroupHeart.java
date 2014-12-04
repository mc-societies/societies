package org.societies.database.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import gnu.trove.set.hash.THashSet;
import org.joda.time.DateTime;
import org.societies.groups.Relation;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberProvider;
import org.societies.groups.rank.Rank;
import org.societies.groups.rank.RankFactory;
import org.societies.groups.setting.Setting;
import org.societies.groups.setting.SettingProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Represents a LazyGroupHeart
 */
public class SQLLazyGroupHeart extends SQLGroupHeart {

    private String name, tag;
    private DateTime created;
    private THashSet<Rank> ranks;
    private Set<Member> members;

    @Inject
    protected SQLLazyGroupHeart(@Assisted Group group, @Named("verify") Setting<Boolean> verifySetting, Setting<Relation> relationSetting, Map<String, Setting<Boolean>> rules, MemberProvider memberProvider, RankFactory rankFactory, Queries queries, ListeningExecutorService service, @Named("predefined-ranks") Set<Rank> defaultRanks, SettingProvider settingProvider) {
        super(group, verifySetting, relationSetting, rules, memberProvider, rankFactory, queries, service, defaultRanks, settingProvider);
    }

    @Override
    public String getName() {
        if (name == null) {
            name = super.getName();
        }

        return name;
    }

    @Override
    public String getTag() {
        if (tag == null) {
            tag = super.getName();
        }

        return tag;
    }

    @Override
    public void setTag(String tag) {
        super.setTag(tag);
        this.tag = tag;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        this.name = name;
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
        super.setCreated(created);
        this.created = created;
    }

    @Override
    public void addRank(Rank rank) {
        getRanks().add(rank);

        super.addRank(rank);
    }

    @Override
    public void removeRank(Rank rank) {
        getRanks().remove(rank);

        super.removeRank(rank);
    }

    @Override
    public Collection<Rank> getRanks() {
        if (ranks == null) {
            ranks = new THashSet<Rank>(super.getRanks());
        }

        return ranks;
    }

    @Override
    public Set<Member> getMembers() {
        if (members == null) {
            members = new THashSet<Member>(super.getMembers());
        }

        return Collections.unmodifiableSet(members);
    }

    @Override
    public void addMember(Member member) {
        getMembers().add(member);

        member.setGroup(this);

        super.addMember(member);
    }

    @Override
    public void removeMember(Member member) {
        if (member.isGroup(this)) {
            member.setGroup(null);
        }

        super.removeMember(member);
    }
}
