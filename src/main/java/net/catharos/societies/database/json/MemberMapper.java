package net.catharos.societies.database.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.inject.Inject;
import net.catharos.groups.*;
import net.catharos.groups.rank.Rank;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Represents a GroupConverter
 */
public class MemberMapper<M extends Member> extends AbstractMapper {

    private final MemberFactory<M> memberFactory;

    @Inject
    public MemberMapper(MemberFactory<M> memberFactory) {
        this.memberFactory = memberFactory;
    }

    public M readMember(JsonParser parser, GroupProvider groupProvider) throws IOException, ExecutionException, InterruptedException {
        parser.nextToken();
        GroupMapper.validateObject(parser);

        UUID uuid = null;
        DateTime created = null, lastActive = null;
        short state = 0;
        Group group = null;

        ArrayList<UUID> ranks = new ArrayList<UUID>();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();

            parser.nextToken();
            if (fieldName.equals("uuid")) {
                uuid = UUID.fromString(parser.getText());
            } else if (fieldName.equals("created")) {
                created = new DateTime(parser.getLongValue());
            } else if (fieldName.equals("state")) {
                state = parser.getShortValue();
            } else if (fieldName.equals("society")) {

                if (parser.getText().equals("null")) {
                    continue;
                }

                group = groupProvider.getGroup(UUID.fromString(parser.getText())).get();
            } else if (fieldName.equals("lastActive")) {
                lastActive = new DateTime(parser.getLongValue());
            } else if (fieldName.equals("ranks")) {
                GroupMapper.validateArray(parser);

                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    ranks.add(UUID.fromString(parser.getText()));
                }
            }
        }

        //Create member

        M member = memberFactory.create(uuid);

        int previousState = member.getState();
        member.setState(DefaultGroup.PREPARE);

        member.setCreated(created);
        member.setLastActive(lastActive);
        member.setState(state);
        member.setGroup(group);

        if (group != null) {
            for (UUID rank : ranks) {
                member.addRank(group.getRank(rank));
            }
        }

        member.setState(previousState);

        return member;
    }

    public void writeMember(JsonGenerator generator, Member member) throws IOException {
        generator.writeStartObject();

        generator.writeStringField("uuid", member.getUUID().toString());
        generator.writeNumberField("created", member.getCreated().getMillis());
        generator.writeNumberField("state", member.getState());
        Group group = member.getGroup();
        if (group != null) {
            generator.writeStringField("society", group.getUUID().toString());
        }
        generator.writeNumberField("lastActive", member.getLastActive().getMillis());

        Set<Rank> ranks = member.getRanks();

        if (!ranks.isEmpty()) {
            generator.writeArrayFieldStart("ranks");
            for (Rank rank : ranks) {
                generator.writeString(rank.getUUID().toString());
            }
            generator.writeEndArray();
        }

        generator.writeEndObject();
    }

    public M readMember(File file, GroupProvider groupProvider) throws IOException, ExecutionException, InterruptedException {
        JsonParser parser = createParser(file);
        M output = readMember(parser, groupProvider);
        parser.close();
        return output;
    }

    public void writeMember(Member member, File file) throws IOException {
        JsonGenerator jg = createGenerator(file);
        writeMember(jg, member);
        jg.close();
    }
}
