package org.societies.database.json;

import com.fasterxml.jackson.core.*;
import com.google.common.collect.Table;
import com.migcomponents.migbase64.Base64;
import net.catharos.lib.core.util.CastSafe;
import net.catharos.lib.core.uuid.UUIDGen;
import org.apache.logging.log4j.Logger;
import org.societies.groups.setting.Setting;
import org.societies.groups.setting.SettingException;
import org.societies.groups.setting.SettingProvider;
import org.societies.groups.setting.subject.Subject;
import org.societies.groups.setting.target.SimpleTarget;
import org.societies.groups.setting.target.Target;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.UUID;

/**
 * Represents a AbstractMapper
 */
public class AbstractMapper {

    private final JsonFactory factory = new JsonFactory();
    protected final Logger logger;
    private final SettingProvider settingProvider;

    public AbstractMapper(Logger logger, SettingProvider settingProvider) {
        this.logger = logger;
        this.settingProvider = settingProvider;
    }

    protected JsonParser createParser(String data) throws IOException {
        return factory.createParser(data);
    }

    protected JsonParser createParser(File file) throws IOException {
        return factory.createParser(file);
    }

    protected JsonGenerator createGenerator(File file) throws IOException {
        return factory.createGenerator(file, JsonEncoding.UTF8);
    }

    protected JsonGenerator createGenerator(OutputStream stream) throws IOException {
        return factory.createGenerator(stream, JsonEncoding.UTF8);
    }

    protected JsonGenerator createGenerator(Writer writer) throws IOException {
        return factory.createGenerator(writer);
    }

    public void readSettings(UUID subject, JsonParser parser, Table<Setting, Target, String> settings) throws IOException {
        validateArray(parser);

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            validateObject(parser);

            Target target = null;
            Setting setting = null;
            String value = null;

            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String settingField = parser.getCurrentName();

                parser.nextToken();
                if (settingField.equals("target")) {
                    target = new SimpleTarget(UUIDGen.toUUID(Base64.decode(parser.getText())));
                } else if (settingField.equals("setting")) {
                    setting = settingProvider.getSetting(parser.getText());
                } else if (settingField.equals("value")) {
                    value = parser.getText();
                }
            }

            if (target == null) {
                target = new SimpleTarget(subject);
            }

            if (setting == null || value == null) {
                continue;
            }

            settings.put(setting, target, value);
        }
    }

    public void writeSettings(Subject subject, JsonGenerator generator, Table<Setting, Target, Object> settings) throws IOException {
        if (settings.isEmpty()) {
            return;
        }

        generator.writeArrayFieldStart("settings");
        for (Table.Cell<Setting, Target, Object> cell : settings.cellSet()) {
            Target target = cell.getColumnKey();
            Setting<Object> setting = CastSafe.toGeneric(cell.getRowKey());
            Object value = cell.getValue();

            String result;

            try {
                result = setting.convertToString(subject, target, value);
            } catch (SettingException ignored) {
                logger.warn("Failed to result setting %s! Subject: %s Target: %s Value: %s", setting, subject, target, value);
                continue;
            }

            generator.writeStartObject();
            UUID targetUUID = target.getUUID();
            if (!targetUUID.equals(subject.getUUID())) {
                generator.writeStringField("target", Base64.encodeToString(UUIDGen.toByteArray(target.getUUID()), false));
            }
            generator.writeStringField("setting", setting.getID());
            generator.writeStringField("value", result);
            generator.writeEndObject();
        }
        generator.writeEndArray();
    }

    public void validateObject(JsonParser parser) throws IOException {
        if (parser.getCurrentToken() != JsonToken.START_OBJECT) {
            throw new IOException("Expected data to start with an Object, but was " + parser.getCurrentToken());
        }
    }

    public void validateArray(JsonParser parser) throws IOException {
        if (parser.getCurrentToken() != JsonToken.START_ARRAY) {
            throw new IOException("Expected data to start with an Array, but was " + parser.getCurrentToken());
        }
    }

}
