package net.catharos.societies.database.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import net.catharos.groups.*;
import net.catharos.groups.rank.Rank;
import net.catharos.groups.rank.RankFactory;
import net.catharos.societies.member.SocietyMember;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Represents a GroupConverter
 */
public class SocietyMapper {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.fullDateTime();

    private final ObjectMapper mapper = new ObjectMapper();

    private final Provider<GroupBuilder> builders;
    private final RankFactory rankFactory;
    private final MemberFactory<SocietyMember> memberFactory;

    @Inject
    public SocietyMapper(Provider<GroupBuilder> builders, RankFactory rankFactory, MemberFactory<SocietyMember> memberFactory) {
        this.builders = builders;
        this.rankFactory = rankFactory;
        this.memberFactory = memberFactory;
    }

    public Group read(InputStream inputStream) throws IOException {
        JsonParser parser = mapper.getFactory().createParser(inputStream);
        Group output = readGroup(parser);
        parser.close();
        return output;
    }

    public Group read(String data) throws IOException {
        JsonParser parser = mapper.getFactory().createParser(data);
        Group output = readGroup(parser);
        parser.close();
        return output;
    }


    public Member readMember(JsonParser parser, GroupProvider groupProvider) throws IOException, ExecutionException, InterruptedException {
        if (parser.nextToken() != JsonToken.START_OBJECT) {
            throw new IOException("Expected data to start with an Object");
        }

        UUID uuid;
        DateTime created, lastActive;
        short state;
        Group society;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();

            parser.nextToken();
            if (fieldName.equals("uuid")) {
                uuid = UUID.fromString(parser.getText());
            } else if (fieldName.equals("created")) {
                created = DATE_FORMAT.parseDateTime(parser.getText());
            } else if (fieldName.equals("state")) {
                state = parser.getShortValue();
            } else if (fieldName.equals("society")) {
                society = groupProvider.getGroup(UUID.fromString(parser.getText())).get();
            } else if (fieldName.equals("lastActive")) {
                lastActive = DATE_FORMAT.parseDateTime(parser.getText());
            }
        }


        return null;
    }

    public Group readGroup(JsonParser parser) throws IOException {
        if (parser.nextToken() != JsonToken.START_OBJECT) {
            throw new IOException("Expected data to start with an Object");
        }

        GroupBuilder builder = builders.get();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();

            JsonToken token = parser.nextToken();
            if (fieldName.equals("uuid")) {
                builder.setUUID(UUID.fromString(parser.getText()));
            } else if (fieldName.equals("name")) {
                builder.setName(parser.getText());
            } else if (fieldName.equals("tag")) {
                builder.setTag(parser.getText());
            } else if (fieldName.equals("created")) {
                builder.setCreated(DATE_FORMAT.parseDateTime(parser.getText()));
            } else if (fieldName.equals("state")) {
                builder.setState(parser.getShortValue());
            } else if (fieldName.equals("settings")) {

            } else if (fieldName.equals("ranks")) {
                if (token != JsonToken.START_ARRAY) {
                    throw new IOException("Expected data to start with an Array");
                }

                ArrayList<Rank> ranks = new ArrayList<Rank>();
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    ranks.add(readRank(parser));
                }

                builder.setRanks(ranks);
            }
        }


        return builder.build();
    }

    public Rank readRank(JsonParser parser) throws IOException {
        if (parser.nextToken() != JsonToken.START_OBJECT) {
            throw new IOException("Expected data to start with an Object");
        }

        UUID uuid = null;
        String name = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();

            parser.nextToken();
            if (fieldName.equals("uuid")) {
                uuid = UUID.fromString(parser.getText());
            } else if (fieldName.equals("name")) {
                name = parser.getText();
            }
        }


        return rankFactory.create(uuid, name, Rank.DEFAULT_PRIORITY);
    }
}
