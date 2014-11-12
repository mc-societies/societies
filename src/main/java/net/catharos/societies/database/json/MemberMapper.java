package net.catharos.societies.database.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.base.Function;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.MemberFactory;
import net.catharos.groups.rank.Rank;
import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.SettingException;
import net.catharos.groups.setting.SettingProvider;
import net.catharos.groups.setting.target.Target;
import org.apache.logging.log4j.Logger;
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
    public MemberMapper(Logger logger, SettingProvider settingProvider, MemberFactory<M> memberFactory) {
        super(logger, settingProvider);
        this.memberFactory = memberFactory;
    }

    public M readMember(JsonParser parser, Function<UUID, Group> groupSupplier) throws IOException, ExecutionException, InterruptedException {
        parser.nextToken();
        validateObject(parser);

        UUID uuid = null;
        DateTime created = null, lastActive = null;
        Group group = null;
        Table<Setting, Target, String> settings = HashBasedTable.create();

        ArrayList<UUID> ranks = new ArrayList<UUID>();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();

            parser.nextToken();
            if (fieldName.equals("uuid")) {
                uuid = UUID.fromString(parser.getText());
            } else if (fieldName.equals("created")) {
                created = new DateTime(parser.getLongValue());
            } else if (fieldName.equals("society")) {

                if (parser.getText().equals("null")) {
                    continue;
                }

                group = groupSupplier.apply(UUID.fromString(parser.getText()));

                if (group == null) {
                    throw new RuntimeException("Could not find group with the uuid " + parser.getText());
                }
            } else if (fieldName.equals("lastActive")) {
                lastActive = new DateTime(parser.getLongValue());
            } else if (fieldName.equals("ranks")) {
                validateArray(parser);

                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    ranks.add(UUID.fromString(parser.getText()));
                }
            } else if (fieldName.equals("settings")) {
                readSettings(parser, settings);
            }
        }

        //Create member

        M member = memberFactory.create(uuid);

        member.complete(false);

        member.setCreated(created);
        member.setLastActive(lastActive);
        member.setGroup(group);

        //beautify
        for (Table.Cell<Setting, Target, String> cell : settings.cellSet()) {
            Setting setting = cell.getRowKey();
            Target target = cell.getColumnKey();
            String value = cell.getValue();

            try {
                member.set(setting, target, setting.convertFromString(group, target, value));
            } catch (SettingException e) {
                logger.warn("Failed to convert setting %s! Subject: %s Target: %s Value: %s", setting, group, target, value);
            }
        }

        if (group != null) {
            for (UUID rank : ranks) {
                member.addRank(group.getRank(rank));
            }
        }

        member.complete();
        return member;
    }

    public void writeMember(JsonGenerator generator, Member member) throws IOException {
        generator.writeStartObject();

        generator.writeStringField("uuid", member.getUUID().toString());
        generator.writeNumberField("created", member.getCreated().getMillis());

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

    public M readMember(File file, Function<UUID, Group> groupSupplier) throws IOException, ExecutionException, InterruptedException {
        JsonParser parser = createParser(file);
        M output = readMember(parser, groupSupplier);
        parser.close();
        return output;
    }

    public void writeMember(Member member, File file) throws IOException {
        JsonGenerator jg = createGenerator(file);
        writeMember(jg, member);
        jg.close();
    }
}
