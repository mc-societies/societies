package org.societies.database.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import gnu.trove.set.hash.THashSet;
import net.catharos.lib.core.uuid.UUIDGen;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.jooq.*;
import org.jooq.types.UShort;
import org.societies.api.group.SocietyException;
import org.societies.bridge.ChatColor;
import org.societies.database.sql.layout.tables.records.MembersRecord;
import org.societies.database.sql.layout.tables.records.RanksRecord;
import org.societies.database.sql.layout.tables.records.SocietiesRecord;
import org.societies.groups.Relation;
import org.societies.groups.group.AbstractGroupHeart;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupHeart;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberProvider;
import org.societies.groups.rank.Rank;
import org.societies.groups.rank.RankFactory;
import org.societies.groups.setting.Setting;
import org.societies.groups.setting.SettingException;
import org.societies.groups.setting.SettingProvider;
import org.societies.groups.setting.subject.Subject;
import org.societies.groups.setting.target.SimpleTarget;
import org.societies.groups.setting.target.Target;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static net.catharos.lib.core.uuid.UUIDGen.toByteArray;
import static org.societies.database.sql.Queries.SELECT_SOCIETY_MEMBERS;
import static org.societies.database.sql.Queries.SELECT_SOCIETY_RANKS;

/**
 * Represents a SQLGroupHeart
 */
public class SQLGroupHeart extends AbstractGroupHeart {

    private final Group group;
    private final Queries queries;
    private final ListeningExecutorService service;
    private final MemberProvider memberProvider;
    private final RankFactory rankFactory;
    private final Set<Rank> defaultRanks;
    private final SettingProvider settingProvider;

    @Inject
    protected SQLGroupHeart(@Assisted Group group,
                            @Named("verify") Setting<Boolean> verifySetting, Setting<Relation> relationSetting,
                            Map<String, Setting<Boolean>> rules, MemberProvider memberProvider, RankFactory rankFactory,
                            Queries queries, ListeningExecutorService service,
                            @Named("predefined-ranks") Set<Rank> defaultRanks, SettingProvider settingProvider) {
        super(rules, verifySetting, relationSetting, group);
        this.group = group;
        this.queries = queries;
        this.service = service;
        this.memberProvider = memberProvider;
        this.rankFactory = rankFactory;
        this.defaultRanks = defaultRanks;
        this.settingProvider = settingProvider;
    }

    private byte[] getByteUUID() {
        return toByteArray(group.getUUID());
    }

    @Override
    public String getName() {
        Select<Record1<String>> query = queries.getQuery(Queries.SELECT_SOCIETY_NAME);
        query.bind(1, getByteUUID());

        Record1<String> record = query.fetch().get(0);
        return record.value1();
    }

    @Override
    public String getTag() {
        Select<Record1<String>> query = queries.getQuery(Queries.SELECT_SOCIETY_TAG);
        query.bind(1, getByteUUID());

        Record1<String> record = query.fetch().get(0);
        return record.value1();
    }

    @Override
    public DateTime getCreated() {
        Select<Record1<Timestamp>> query = queries.getQuery(Queries.SELECT_SOCIETY_CREATED);
        query.bind(1, getByteUUID());

        Record1<Timestamp> record = query.fetch().get(0);
        return new DateTime(record.value1());
    }

    @Override
    public Collection<Rank> getRanks() {
        THashSet<Rank> ranks = new THashSet<Rank>();
        Select<Record3<byte[], String, Short>> rankQuery = queries.getQuery(SELECT_SOCIETY_RANKS);
        rankQuery.bind(1, getByteUUID());


        for (Record3<byte[], String, Short> rankRecord : rankQuery.fetch()) {
            Rank rank = loadRank(group, rankRecord);
            ranks.add(rank);
        }
        return CollectionUtils.union(defaultRanks, ranks);
    }

    private Rank loadRank(Group group, Record3<byte[], String, Short> rankRecord) {
        UUID uuid = UUIDGen.toUUID(rankRecord.value1());
        String name = rankRecord.value2();
        Short priority = rankRecord.value3();

        Rank rank = rankFactory.create(uuid, name, priority, group);
        rank.unlink();

        loadSettings(rank, rankRecord.value1(), queries.getQuery(Queries.SELECT_RANK_SETTINGS));

        rank.link();
        return rank;
    }

    //beautify
    private void loadSettings(Subject subject, byte[] uuid, Select<Record3<byte[], UShort, byte[]>> query) {
        query.bind(1, uuid);

        for (Record3<byte[], UShort, byte[]> settingRecord : query.fetch()) {
            int settingID = settingRecord.value2().intValue();

            Setting setting = settingProvider.getSetting(settingID);

            if (setting == null) {
                System.out.println("Failed to convert setting " + settingID);
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
            } catch (SettingException ignored) {
                continue;
            }

            subject.set(setting, target, value);
        }
    }

    @Override
    public Set<Member> getMembers() {
        THashSet<Member> members = new THashSet<Member>();
        Select<Record1<byte[]>> query = queries.getQuery(SELECT_SOCIETY_MEMBERS);

        query.bind(1, getByteUUID());

        for (Record1<byte[]> member : query.fetch()) {
            try {
                UUID memberUUID = UUIDGen.toUUID(member.value1());
                members.add(memberProvider.getMember(memberUUID).get());
            } catch (InterruptedException e) {
                throw new SocietyException(e, "Failed to add member to group!");
            } catch (ExecutionException e) {
                throw new SocietyException(e, "Failed to add member to group!");
            }
        }
        return members;
    }

    @Override
    public void setTag(final String tag) {
        service.submit(new Callable<GroupHeart>() {
            @Override
            public GroupHeart call() throws Exception {
                Update<SocietiesRecord> query = queries.getQuery(Queries.UPDATE_SOCIETY_TAG);

                query.bind(1, tag);
                query.bind(2, ChatColor.stripColor(tag));
                query.bind(3, UUIDGen.toByteArray(group.getUUID()));


                query.execute();
                return group;
            }
        });
    }

    @Override
    public void setName(final String name) {
        service.submit(new Callable<GroupHeart>() {
            @Override
            public GroupHeart call() throws Exception {
                Update<SocietiesRecord> query = queries.getQuery(Queries.UPDATE_SOCIETY_NAME);

                query.bind(1, name);
                query.bind(2, UUIDGen.toByteArray(group.getUUID()));

                query.execute();
                return group;
            }
        });
    }

    @Override
    public void setCreated(final DateTime created) {
        service.submit(new Callable<GroupHeart>() {
            @Override
            public GroupHeart call() throws Exception {
                Update<MembersRecord> query = queries.getQuery(Queries.UPDATE_MEMBER_LAST_ACTIVE);

                query.bind(1, UUIDGen.toByteArray(group.getUUID()));
                query.bind(2, new Timestamp(created.getMillis()));

                query.execute();

                return group;
            }
        });
    }

    @Override
    public void addRank(final Rank rank) {
        if (rank.isStatic()) {
            return;
        }

        //beautify duplicate
        service.submit(new Callable<GroupHeart>() {
            @Override
            public GroupHeart call() throws Exception {
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

                query = queries.getQuery(Queries.INSERT_SOCIETY_RANK);
                query.bind(1, UUIDGen.toByteArray(group.getUUID()));
                query.bind(2, UUIDGen.toByteArray(rank.getUUID()));
                query.execute();
                return group;
            }
        });
    }

    @Override
    public void removeRank(final Rank rank) {
        super.removeRank(rank);
        service.submit(new Callable<Rank>() {
            @Override
            public Rank call() throws Exception {
                Query query = queries.getQuery(Queries.DROP_RANK_IN_SOCIETIES);
                query.bind(1, UUIDGen.toByteArray(rank.getUUID()));
                query.execute();

                query = queries.getQuery(Queries.DROP_RANK_IN_MEMBERS);
                query.bind(1, UUIDGen.toByteArray(rank.getUUID()));
                query.execute();

                query = queries.getQuery(Queries.DROP_RANK);
                query.bind(1, UUIDGen.toByteArray(rank.getUUID()));
                query.execute();
                return rank;
            }
        });
    }

    @Override
    public void addMember(Member member) {
        member.setGroup(this);
    }

    @Override
    public void removeMember(Member member) {
        member.setGroup(null);
    }

    @Override
    public Group getHolder() {
        return group;
    }
}
