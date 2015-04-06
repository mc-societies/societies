package org.societies.database.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.migcomponents.migbase64.Base64;
import org.apache.logging.log4j.Logger;
import org.bukkit.Server;
import org.joda.time.DateTime;
import org.societies.api.group.Society;
import org.societies.api.math.Location;
import org.societies.groups.DefaultRelation;
import org.societies.groups.Relation;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupBuilder;
import org.societies.groups.rank.Rank;
import org.societies.groups.rank.RankFactory;
import org.societies.util.uuid.UUIDGen;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a GroupMapper
 */
public class GroupMapper extends AbstractMapper {

    private final Provider<GroupBuilder> builders;
    private final RankFactory rankFactory;

    private final Logger logger;

    @Inject
    public GroupMapper(Provider<GroupBuilder> builders, RankFactory rankFactory, Server worldResolver, Logger logger) {
        super(worldResolver);
        this.builders = builders;
        this.rankFactory = rankFactory;
        this.logger = logger;
    }

    public Group readGroup(JsonNode node) throws IOException {

        GroupBuilder builder = builders.get();


        builder.setUUID(toUUID(node.path("uuid")));
        builder.setName(node.path("name").asText());
        builder.setTag(node.path("tag").asText());
        builder.setCreated(new DateTime(node.path("created").asLong()));


        JsonNode relationsNode = node.path("relations");


        Group group = builder.build();

        group.unlink();


        for (JsonNode relationNode : relationsNode) {
            UUID target = toUUID(relationNode.get("target"));
            int type = relationNode.get("type").asInt();

            group.setRawRelation(target, new DefaultRelation(group.getUUID(), target, Relation.Type.getType((byte) type)));
        }

        Society society = group.get(Society.class);
        society.setBalance(node.path("balance").asDouble());
        society.setFriendlyFire(node.path("ff").asBoolean());
        society.setVerified(node.path("verified").asBoolean());

        JsonNode home = node.path("home");

        if (!home.isMissingNode()) {
            society.setHome(toLocation(home));
        }

        for (JsonNode rankNode : node.path("ranks")) {
            group.addRank(readRank(rankNode, group));
        }

        group.link();

        return group;
    }


    private Rank readRank(JsonNode node, Group owner) throws IOException {
        String name = node.path("name").asText();
        int priority = node.path("priority").asInt();

        Rank rank = rankFactory.create(name, priority, owner);
        for (JsonNode rule : node.path("rules")) {
            rank.addRule(rule.asText());
        }

        rank.link();

        return rank;
    }

    public JsonNode createNode(Group group) throws IOException {
        ObjectNode node = mapper.createObjectNode();

        node.put("uuid", Base64.encodeToString(UUIDGen.toByteArray(group.getUUID()), false));
        node.put("name", group.getName());
        node.put("tag", group.getTag());
        node.put("created", group.getCreated().getMillis());

        Society society = group.get(Society.class);

        node.put("verified", society.isVerified());
        node.put("balance", society.getBalance());
        node.put("ff", society.isFriendlyFire());

        Optional<Location> home = society.getHome();
        if (home.isPresent()) {
            node.set("home", toNode(home.get()));
        }

        Collection<Relation> relations = group.getRelations();

        ArrayNode relationsNode = node.putArray("relations");

        for (Relation relation : relations) {
            ObjectNode relationNode = relationsNode.addObject();
            relationNode.put("target", toText(relation.getOpposite(group.getUUID())));
            relationNode.put("type", relation.getType().getID());
        }


        Collection<Rank> ranks = group.getRanks();
        if (!ranks.isEmpty()) {
            ArrayNode ranksNode = node.putArray("ranks");
            for (Rank rank : ranks) {
                if (rank.isStatic()) {
                    continue;
                }

                ranksNode.add(createNode(rank));
            }
        }

        return node;
    }

    private JsonNode createNode(Rank rank) throws IOException {
        ObjectNode node = mapper.createObjectNode();

        node.put("name", rank.getName());
        node.put("priority", rank.getPriority());

        Set<String> rules = rank.getRules();
        ArrayNode rulesNode = node.putArray("rules");

        for (String rule : rules) {
            rulesNode.add(rule);
        }

        return node;
    }

    public Group readGroup(File file) throws IOException {
        JsonNode node = createNode(file);
        return readGroup(node);
    }

    public void createNode(Group group, OutputStream stream) throws IOException {
        JsonGenerator jg = createGenerator(stream);
        JsonNode node = createNode(group);
        mapper.writeTree(jg, node);
        if (isEmpty(node)) {
            logger.warn("Empty group node!");
            return;
        }
        jg.close();
    }

    public void createNode(Group group, File file) throws IOException {
        JsonGenerator jg = createGenerator(file);
        JsonNode node = createNode(group);
        mapper.writeTree(jg, node);
        if (isEmpty(node)) {
            logger.warn("Empty group node!");
            return;
        }
        jg.close();
    }
}
