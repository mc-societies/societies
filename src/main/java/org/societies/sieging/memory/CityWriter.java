package org.societies.sieging.memory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.google.inject.Inject;
import com.migcomponents.migbase64.Base64;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.City;
import org.societies.api.sieging.Land;
import org.societies.bridge.Location;
import org.societies.bridge.WorldResolver;
import org.societies.database.json.AbstractMapper;
import org.societies.util.uuid.UUIDGen;
import org.societies.util.uuid.UUIDStorage;

import javax.inject.Named;
import java.io.IOException;

/**
 * Represents a CityWriter
 */
public class CityWriter extends AbstractMapper {

    private final UUIDStorage cityStorage;

    @Inject
    public CityWriter(@Named("cities") UUIDStorage cityStorage, WorldResolver worldResolver) {
        super(worldResolver);
        this.cityStorage = cityStorage;
    }


    public void writeBesieger(Besieger besieger) throws IOException {
        JsonGenerator generator = createGenerator(cityStorage.getFile(besieger.getUUID()));
        writeBesieger(generator, besieger);

        generator.flush();
        generator.close();
    }

    public void writeBesieger(JsonGenerator writer, Besieger besieger) throws IOException {
        writer.writeStartObject();

        writer.writeArrayFieldStart("cities");
        writeCities(writer, besieger);
        writer.writeEndArray();

        writer.writeArrayFieldStart("unallocated");
        writeLands(writer, besieger.getUnallocatedLands());
        writer.writeEndArray();

        writer.writeEndObject();
    }

    public void writeCities(JsonGenerator writer, Besieger besieger) throws IOException {
        for (City city : besieger.getCities()) {
            writeCity(writer, city);
        }
    }

    public void writeCity(JsonGenerator writer, City city) throws IOException {
        writer.writeStartObject();

        writer.writeStringField("uuid", Base64.encodeToString(UUIDGen.toByteArray(city.getUUID()), false));
        writer.writeStringField("name", city.getName());

        Location location = city.getLocation();
        writer.writeObjectFieldStart("location");
        writer.writeStringField("world", location.getWorld().getName());
        writer.writeNumberField("x", location.getX());
        writer.writeNumberField("y", location.getY());
        writer.writeNumberField("z", location.getZ());
        writer.writeEndObject();

        writer.writeArrayFieldStart("lands");
        writeLands(writer, city.getLands());
        writer.writeEndArray();

        writer.writeStringField("owner", Base64.encodeToString(UUIDGen.toByteArray(city.getOwner().getUUID()), false));
        writer.writeNumberField("founded", city.getFounded().getMillis());

        writer.writeEndObject();
    }

    private void writeLands(JsonGenerator writer, Iterable<Land> lands) throws IOException {
        for (Land land : lands) {
            writer.writeStartObject();
            writer.writeStringField("uuid", Base64.encodeToString(UUIDGen.toByteArray(land.getUUID()), false));
            writer.writeEndObject();
        }
    }
}
