package org.societies.bukkit.converter;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.p000ison.dev.simpleclans2.SimpleClans;
import com.p000ison.dev.simpleclans2.api.clan.Clan;
import com.p000ison.dev.simpleclans2.api.clan.ClanManager;
import com.p000ison.dev.simpleclans2.api.clanplayer.ClanPlayer;
import com.p000ison.dev.simpleclans2.clanplayer.CraftClanPlayerManager;
import gnu.trove.map.hash.THashMap;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.societies.api.math.Location;
import org.societies.api.group.Society;
import org.societies.converter.AbstractConverter;
import org.societies.groups.Relation;
import org.societies.groups.RelationFactory;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupBuilder;
import org.societies.groups.group.GroupPublisher;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberFactory;
import org.societies.groups.member.MemberPublisher;
import org.societies.groups.rank.Rank;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a SimpleClansConverter
 */
public class SimpleClans2Converter extends AbstractConverter {

    private final SimpleClans simpleClans;

    private final Logger logger;

    @Inject
    public SimpleClans2Converter(SimpleClans simpleClans,
                                 MemberPublisher memberPublisher,
                                 GroupPublisher groupPublisher,
                                 MemberFactory memberFactory,
                                 Provider<GroupBuilder> groupBuilder,
                                 RelationFactory relationFactory,
                                 @Named("super-default-rank") Rank superDefaultRank, Logger logger) {
        super(memberFactory, groupBuilder, relationFactory, superDefaultRank, memberPublisher, groupPublisher);
        this.simpleClans = simpleClans;
        this.logger = logger;
    }

    @Override
    public void convert() {
        logger.info("Found SimpleClans2. Converting...");
        ClanManager clanManager = simpleClans.getClanManager();
        CraftClanPlayerManager clanPlayerManager = simpleClans.getClanPlayerManager();

        Set<Clan> clans = clanManager.getClans();

        THashMap<String, Group> groups = new THashMap<String, Group>();

        // Convert groups
        for (Clan clan : clans) {
            try {
                GroupBuilder builder = getGroupBuilder().get();

                builder.setName(clan.getName());
                builder.setTag(clan.getTag());
                builder.setCreated(new DateTime(clan.getFoundedDate()));

                Group group = builder.build();
                publish(group);

                Society society = group.get(Society.class);

                society.setBalance(clan.getBalance());
                society.setHome(new Location(clan.getFlags().getHomeLocation()));

                groups.put(group.getTag(), group);
            } catch (RuntimeException e) {
                logger.warn("Failed to process {0}", clan, e);
            }
        }

        Set<ClanPlayer> clanPlayers = clanPlayerManager.getClanPlayers();

        // Convert members
        for (ClanPlayer cp : clanPlayers) {
            try {
                UUID uuid = findUUID(cp.getName());
                Member member = getMemberFactory().create(uuid);
                publish(member);

                member.setLastActive(new DateTime(cp.getLastSeenDate()));
                member.setCreated(new DateTime(cp.getJoinDate()));

                Clan clan = cp.getClan();

                if (clan != null) {
                    member.setGroup(groups.get(clan.getTag()));

                    if (cp.isLeader()) {
                        member.addRank(getSuperDefaultRank());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // Add allies/rivals
        for (Clan clan : clanManager.getClans()) {
            Group source = groups.get(clan.getTag());

            Set<Clan> allies = clan.getAllies();
            Set<Clan> rivals = clan.getRivals();

            for (Clan ally : allies) {
                Group target = groups.get(ally.getTag());
                source.setRelation(target, getRelationFactory().create(source, target, Relation.Type.ALLIED));
            }

            for (Clan rival : rivals) {
                Group target = groups.get(rival.getTag());
                source.setRelation(target, getRelationFactory().create(source, target, Relation.Type.RIVALED));
            }
        }
    }
}
