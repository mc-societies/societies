package org.societies.database.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Function;
import com.google.inject.Inject;
import org.joda.time.DateTime;
import org.societies.api.member.SocietyMember;
import org.societies.bridge.WorldResolver;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberFactory;
import org.societies.groups.rank.Rank;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Represents a GroupConverter
 */
public class MemberMapper extends AbstractMapper {

    private final MemberFactory memberFactory;

    @Inject
    public MemberMapper(MemberFactory memberFactory, WorldResolver worldResolver) {
        super(worldResolver);
        this.memberFactory = memberFactory;
    }

    public Member readMember(JsonNode node, Function<UUID, Group> groupSupplier) throws IOException, ExecutionException, InterruptedException {
        Member member = memberFactory.create(toUUID(node.path("uuid")));

        member.unlink();

        member.setCreated(new DateTime(node.path("created").asLong()));
        member.setLastActive(new DateTime(node.path("lastActive").asLong()));
        Group group = groupSupplier.apply(toUUID(node.path("society")));
        member.setGroup(group);

        SocietyMember society = member.get(SocietyMember.class);
        society.setFirendlyFire(node.path("ff").asBoolean());


        if (group != null) {
            for (JsonNode rank : node.path("ranks")) {
                member.addRank(group.getRank(toUUID(rank)));
            }
        }

        member.link();
        return member;
    }

    public JsonNode writeMember( Member member) throws IOException {
        ObjectNode node = mapper.createObjectNode();

        node.put("uuid", toText(member.getUUID()));
        node.put("created", member.getCreated().getMillis());

        Group group = member.getGroup();
        if (group != null) {
            node.put("society", toText(group.getUUID()));
        }
        node.put("lastActive", member.getLastActive().getMillis());

        Set<Rank> ranks = member.getRanks();

        if (!ranks.isEmpty()) {
            ArrayNode ranksNode = node.putArray("ranks");
            for (Rank rank : ranks) {
                ranksNode.add(toText(rank.getUUID()));
            }
        }

        return node;
    }

    public Member readMember(File file, Function<UUID, Group> groupSupplier) throws IOException, ExecutionException, InterruptedException {
        JsonNode parser = createNode(file);
        return readMember(parser, groupSupplier);
    }

    public void writeMember(Member member, OutputStream stream) throws IOException {
        JsonGenerator jg = createGenerator(stream);
        JsonNode node = writeMember(member);
        mapper.writeTree(jg, node);
        jg.close();
    }

    public void writeMember(Member member, File file) throws IOException {
        JsonGenerator jg = createGenerator(file);
        JsonNode node = writeMember(member);
        mapper.writeTree(jg, node);
        jg.close();
    }
}
