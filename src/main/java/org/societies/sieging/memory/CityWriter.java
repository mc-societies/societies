package org.societies.sieging.memory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.migcomponents.migbase64.Base64;
import org.bukkit.Server;
import org.societies.api.math.Location;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.City;
import org.societies.api.sieging.Land;
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
    public CityWriter(@Named("cities") UUIDStorage cityStorage, Server worldResolver) {
        super(worldResolver);
        this.cityStorage = cityStorage;
    }

    public void writeBesieger(Besieger besieger) throws IOException {
        JsonGenerator generator = createGenerator(cityStorage.getFile(besieger.getUUID()));

        ObjectNode node = createBesiegerNode(besieger);

        generator.writeTree(node);

        generator.flush();
        generator.close();
    }

    public ObjectNode createBesiegerNode(Besieger besieger) throws IOException {
        ObjectNode node = mapper.createObjectNode();

        ArrayNode citiesNode = node.putArray("cities");

        for (City city : besieger.getCities()) {
            citiesNode.add(createCityNode(city));
        }

        node.set("unallocated", createLandsNode(besieger.getUnallocatedLands()));

        return node;
    }

    public ObjectNode createCityNode(City city) throws IOException {
        ObjectNode node = mapper.createObjectNode();


        node.put("uuid", Base64.encodeToString(UUIDGen.toByteArray(city.getUUID()), false));
        node.put("name", city.getName());

        Location location = city.getLocation();
        node.set("location", toNode(location));

        node.set("lands", createLandsNode(city.getLands()));

        node.put("owner", toText(city.getOwner().getUUID()));
        node.put("founded", city.getFounded().getMillis());

        return node;
    }

    private ArrayNode createLandsNode(Iterable<Land> lands) throws IOException {
        ArrayNode landsNode = mapper.createArrayNode();

        for (Land land : lands) {
            ObjectNode landNode = landsNode.addObject();
            landNode.put("uuid", toText(land.getUUID()));
            landNode.put("origin", toText(land.getOrigin()));
        }

        return landsNode;
    }
}
