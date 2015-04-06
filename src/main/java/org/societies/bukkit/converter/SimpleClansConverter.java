package org.societies.bukkit.converter;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import gnu.trove.map.hash.THashMap;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
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

import java.util.List;
import java.util.UUID;

/**
 * Represents a SimpleClansConverter
 */
public class SimpleClansConverter extends AbstractConverter {
    private final SimpleClans simpleClans;

    private final Logger logger;

    @Inject
    public SimpleClansConverter(SimpleClans simpleClans,
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
        logger.info("Found SimpleClans. Converting...");
        ClanManager clanManager = simpleClans.getClanManager();

        List<ClanPlayer> clanPlayers = clanManager.getAllClanPlayers();

        THashMap<String, Group> groups = new THashMap<String, Group>();

        // Convert groups
        for (Clan clan : clanManager.getClans()) {
            try {
                GroupBuilder builder = getGroupBuilder().get();

                builder.setName(clan.getName());
                builder.setTag(clan.getTag());
                builder.setCreated(new DateTime(clan.getFounded()));

                Group group = builder.build();
                publish(group);

                Society society = group.get(Society.class);

                society.setBalance(clan.getBalance());
                society.setHome(new Location(clan.getHomeLocation()));

                groups.put(group.getTag(), group);
            } catch (RuntimeException e) {
                logger.warn("Failed to process {0}", clan, e);
            }
        }

        // Convert members
        for (ClanPlayer cp : clanPlayers) {
            try {
                UUID uuid = findUUID(cp.getName());
                Member member = getMemberFactory().create(uuid);
                publish(member);


                member.setLastActive(new DateTime(cp.getLastSeen()));
                member.setCreated(new DateTime(cp.getJoinDate()));

                Clan clan = cp.getClan();

                if (clan != null) {
                    member.setGroup(groups.get(clan.getTag()));

                    if (cp.isLeader()) {
                        member.addRank(getSuperDefaultRank());
                    }
                }

            } catch (Exception e) {
                logger.warn("Failed to process {0}", cp, e);
            }
        }

        // Add allies/rivals
        for (Clan clan : clanManager.getClans()) {
            Group source = groups.get(clan.getTag());

            List<String> allies = clan.getAllies();
            List<String> rivals = clan.getRivals();

            for (String ally : allies) {
                Group target = groups.get(ally);
                source.setRelation(target, getRelationFactory().create(source, target, Relation.Type.ALLIED));
            }

            for (String rival : rivals) {
                Group target = groups.get(rival);
                source.setRelation(target, getRelationFactory().create(source, target, Relation.Type.RIVALED));
            }
        }
    }
}
