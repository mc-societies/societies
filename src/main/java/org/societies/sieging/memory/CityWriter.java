package org.societies.sieging.memory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.google.inject.Inject;
import com.migcomponents.migbase64.Base64;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.lib.core.uuid.UUIDStorage;
import org.apache.logging.log4j.Logger;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.City;
import org.societies.api.sieging.Land;
import org.societies.bridge.Location;
import org.societies.database.json.AbstractMapper;
import org.societies.groups.setting.SettingProvider;

import javax.inject.Named;
import java.io.IOException;

/**
 * Represents a CityWriter
 */
public class CityWriter extends AbstractMapper {

    private final UUIDStorage cityStorage;

    @Inject
    public CityWriter(@Named("cities") UUIDStorage cityStorage, Logger logger, SettingProvider settingProvider) {
        super(logger, settingProvider);
        this.cityStorage = cityStorage;
    }

    public void writeCities(Besieger besieger) throws IOException {
        JsonGenerator generator = createGenerator(cityStorage.getFile(besieger.getUUID()));
        writeCities(generator, besieger);

        generator.flush();
        generator.close();
    }

    public void writeCities(JsonGenerator writer, Besieger besieger) throws IOException {
        writer.writeStartArray();

        for (City city : besieger.getCities()) {
            writeCity(writer, city);
        }

        writer.writeEndArray();
    }

    public void writeCity(JsonGenerator writer, City city) throws IOException {
        writer.writeStartObject();

        writer.writeStringField("uuid", Base64.encodeToString(UUIDGen.toByteArray(city.getUUID()), false));
        writer.writeStringField("name", city.getName());

        Location location = city.getLocation();
        writer.writeObjectFieldStart("location");
        writer.writeNumberField("x", location.getX());
        writer.writeNumberField("y", location.getY());
        writer.writeNumberField("z", location.getZ());
        writer.writeEndObject();

        writer.writeArrayFieldStart("lands");
        for (Land land : city.getLands()) {
            writer.writeStartObject();
            writer.writeStringField("uuid", Base64.encodeToString(UUIDGen.toByteArray(land.getUUID()), false));
            writer.writeEndObject();
        }
        writer.writeEndArray();

        writer.writeStringField("owner", Base64.encodeToString(UUIDGen.toByteArray(city.getOwner().getUUID()), false));
        writer.writeNumberField("founded", city.getFounded().getMillis());

        writeSettings(city, writer, city.getSettings());

        writer.writeEndObject();
    }
}
