package net.catharos.societies.database.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.GroupProvider;
import net.catharos.groups.Member;
import net.catharos.groups.MemberFactory;
import net.catharos.groups.rank.Rank;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

    public M readMember(File file, GroupProvider groupProvider) throws IOException, ExecutionException, InterruptedException {
        JsonParser parser = createParser(file);
        M output = readMember(parser, groupProvider);
        parser.close();
        return output;
    }

    public M readMember(JsonParser parser, GroupProvider groupProvider) throws IOException, ExecutionException, InterruptedException {
        if (parser.nextToken() != JsonToken.START_OBJECT) {
            throw new IOException("Expected data to start with an Object");
        }

        UUID uuid = null;
        DateTime created = null, lastActive = null;
        short state = 0;
        Group group = null;

        ArrayList<UUID> ranks = new ArrayList<UUID>();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();

            JsonToken token = parser.nextToken();
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
                if (token != JsonToken.START_ARRAY) {
                    throw new IOException("Expected data to start with an Array");
                }

                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    ranks.add(UUID.fromString(parser.getText()));
                }
            }
        }

        M member = memberFactory.create(uuid);
        member.setCreated(created);
        member.setLastActive(lastActive);
        member.setState(state);
        member.setGroup(group);

        if (group != null) {
            for (UUID rank : ranks) {
                member.addRank(group.getRank(rank));
            }
        }

        return member;
    }

    public void writeMember(Member member, File file) throws IOException {
        JsonGenerator jg = createGenerator(file);
        writeMember(jg, member);

        jg.close();
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
        generator.writeNumberField("lastActive", (short) member.getState());
        generator.writeArrayFieldStart("ranks");

        for (Rank rank : member.getRanks()) {
            generator.writeString(rank.getUUID().toString());
        }

        generator.writeEndArray();

        generator.writeEndObject();
    }

}
