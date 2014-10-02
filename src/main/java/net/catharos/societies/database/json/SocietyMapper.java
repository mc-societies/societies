package net.catharos.societies.database.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Table;
import com.google.inject.Inject;
import com.google.inject.Provider;
import gnu.trove.set.hash.THashSet;
import net.catharos.groups.*;
import net.catharos.groups.rank.Rank;
import net.catharos.groups.rank.RankFactory;
import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.SettingProvider;
import net.catharos.groups.setting.target.SimpleTarget;
import net.catharos.groups.setting.target.Target;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Represents a GroupConverter
 */
public class SocietyMapper<M extends Member> {

    private final ObjectMapper mapper = new ObjectMapper();

    private final Provider<GroupBuilder> builders;
    private final RankFactory rankFactory;
    private final MemberFactory<M> memberFactory;
    private final SettingProvider settingProvider;

    @Inject
    public SocietyMapper(Provider<GroupBuilder> builders, RankFactory rankFactory, MemberFactory<M> memberFactory, SettingProvider settingProvider) {
        this.builders = builders;
        this.rankFactory = rankFactory;
        this.memberFactory = memberFactory;
        this.settingProvider = settingProvider;
    }

    public Group readGroup(File file) throws IOException {
        JsonParser parser = mapper.getFactory().createParser(file);
        Group output = readGroup(parser);
        parser.close();
        return output;
    }

    public Group readGroup(String data) throws IOException {
        JsonParser parser = mapper.getFactory().createParser(data);
        Group output = readGroup(parser);
        parser.close();
        return output;
    }

    public M readMember(File file, GroupProvider groupProvider) throws IOException, ExecutionException, InterruptedException {
        JsonParser parser = mapper.getFactory().createParser(file);
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
        JsonGenerator jg = mapper.getFactory().createGenerator(file, JsonEncoding.UTF8);
        jg.useDefaultPrettyPrinter();
        writeMember(jg, member);

        jg.close();
    }

    public void writeMember(JsonGenerator generator, Member member) throws IOException {
        generator.writeStartObject();

        generator.writeStringField("uuid", member.getUUID().toString());
        generator.writeNumberField("created", member.getCreated().getMillis());
        generator.writeNumberField("state", member.getState());
        generator.writeNumberField("society", member.getCreated().getMillis());
        generator.writeNumberField("lastActive", (short) member.getState());
        generator.writeArrayFieldStart("ranks");

        for (Rank rank : member.getRanks()) {
            generator.writeString(rank.getUUID().toString());
        }

        generator.writeEndArray();

        generator.writeEndObject();
    }

    public Set<Group> readGroups(File file) throws IOException {
        JsonParser parser = mapper.getFactory().createParser(file);
        Set<Group> output = readGroups(parser);
        parser.close();
        return output;
    }

    public Set<Group> readGroups(JsonParser parser) throws IOException {
        if (parser.nextToken() != JsonToken.START_ARRAY) {
            throw new IOException("Expected data to start with an Object");
        }

        THashSet<Group> groups = new THashSet<Group>();

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            groups.add(readGroup(parser));
        }

        return groups;
    }

    public Group readGroup(JsonParser parser) throws IOException {
        if (parser.nextToken() != JsonToken.START_OBJECT) {
            throw new IOException("Expected data to start with an Object");
        }

        GroupBuilder builder = builders.get();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String groupField = parser.getCurrentName();

            JsonToken token = parser.nextToken();
            if (groupField.equals("uuid")) {
                builder.setUUID(UUID.fromString(parser.getText()));
            } else if (groupField.equals("name")) {
                builder.setName(parser.getText());
            } else if (groupField.equals("tag")) {
                builder.setTag(parser.getText());
            } else if (groupField.equals("created")) {
                builder.setCreated(new DateTime(parser.getLongValue()));
            } else if (groupField.equals("state")) {
                builder.setState(parser.getShortValue());
            } else if (groupField.equals("settings")) {
                if (token != JsonToken.START_ARRAY) {
                    throw new IOException("Expected data to start with an Array");
                }

                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    if (parser.nextToken() != JsonToken.START_OBJECT) {
                        throw new IOException("Expected data to start with an Object");
                    }

                    Target target = null;
                    Setting setting = null;
                    byte[] value = new byte[0];

                    while (parser.nextToken() != JsonToken.END_OBJECT) {
                        String settingField = parser.getCurrentName();

                        if (settingField.equals("target")) {
                            target = new SimpleTarget(UUID.fromString(parser.getText()));
                        } else if (settingField.equals("setting")) {
                            setting = settingProvider.getSetting(parser.getIntValue());
                        } else if (settingField.equals("value")) {
                            value = parser.getBinaryValue();
                        }
                    }

                    builder.put(setting, target, value);
                }
            } else if (groupField.equals("ranks")) {
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

    public void writeGroup(Group group, File file) throws IOException {
        JsonGenerator jg = mapper.getFactory().createGenerator(file, JsonEncoding.UTF8);
        jg.useDefaultPrettyPrinter();
        writeGroup(jg, group);

        jg.close();
    }

    public String writeGroup(Group group) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonGenerator jg = mapper.getFactory().createGenerator(stringWriter);
        jg.useDefaultPrettyPrinter();
        writeGroup(jg, group);

        jg.close();

        return stringWriter.toString();
    }

    public void writeGroup(JsonGenerator generator, Group group) throws IOException {
        generator.writeStartObject();

        generator.writeStringField("uuid", group.getUUID().toString());
        generator.writeStringField("name", group.getName());
        generator.writeStringField("tag", group.getTag());
        generator.writeNumberField("created", group.getCreated().getMillis());
        generator.writeNumberField("state", (short) group.getState());
        generator.writeArrayFieldStart("ranks");

        for (Rank rank : group.getRanks()) {
            writeRank(generator, rank);
        }

        generator.writeEndArray();

        generator.writeArrayFieldStart("settings");

        for (Table.Cell<Setting, Target, Object> cell : group.getSettings().cellSet()) {
            generator.writeStartObject();
            Target target = cell.getColumnKey();
            Setting setting = cell.getRowKey();

            generator.writeStringField("target", target.getUUID().toString());

            generator.writeNumberField("setting", setting.getID());
            generator.writeBinaryField("value", setting.convert(group, target, cell.getValue()));
            generator.writeEndObject();
        }

        generator.writeEndArray();

        generator.writeEndObject();
    }


    public Rank readRank(JsonParser parser) throws IOException {
        if (parser.getCurrentToken() != JsonToken.START_OBJECT) {
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

    public void writeRank(JsonGenerator generator, Rank rank) throws IOException {
        generator.writeStartObject();

        generator.writeStringField("uuid", rank.getUUID().toString());
        generator.writeStringField("name", rank.getName());
        generator.writeEndObject();
    }
}
