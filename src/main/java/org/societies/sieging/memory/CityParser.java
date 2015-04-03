package org.societies.sieging.memory;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Function;
import com.google.inject.Inject;
import gnu.trove.set.hash.THashSet;
import org.joda.time.DateTime;
import org.societies.api.sieging.*;
import org.societies.bridge.Location;
import org.societies.bridge.WorldResolver;
import org.societies.database.json.AbstractMapper;
import org.societies.util.uuid.UUIDStorage;

import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a CityMapper
 */
public class CityParser extends AbstractMapper {

    private final UUIDStorage cityStorage;
    private final CityPublisher cityPublisher;

    private final Function<Integer, Double> cityFunction;

    @Inject
    public CityParser(WorldResolver worldResolver,
                      @Named("cities") UUIDStorage cityStorage,
                      CityPublisher cityPublisher,
                      @Named("city-function") Function<Integer, Double> cityFunction) {
        super(worldResolver);
        this.cityStorage = cityStorage;
        this.cityPublisher = cityPublisher;
        this.cityFunction = cityFunction;
    }

    public Set<City> readBesieger(Besieger owner) throws IOException {
        File file = cityStorage.getFile(owner.getUUID());

        if (!file.exists()) {
            return Collections.emptySet();
        }

        JsonNode node = createNode(file);
        return readBesieger(node, owner);
    }

    public Set<City> readBesieger(JsonNode node, Besieger empty) throws IOException {


        for (JsonNode landsNode : node.path("unallocated")) {
            Set<Land> lands = parseLands(landsNode);
            for (Land land : lands) {
                empty.addUnallocatedLand(land);
            }
        }

        return readCities(node.path("cities"), empty);
    }

    public Set<City> readCities(JsonNode node, Besieger empty) throws IOException {

        THashSet<City> cities = new THashSet<City>();


        for (JsonNode cityNode : node) {
            City city = readCity(cityNode, empty);
            city.link();

            cities.add(city);
        }

        return cities;
    }

    public City readCity(JsonNode node, Besieger owner) throws IOException {
        UUID uuid = toUUID(node.path("uuid"));
        String name = node.path("name").asText();
        Location location = toLocation(node.path("location"));
        Set<Land> lands = parseLands(node.path("lands"));
        UUID ownerUUID = toUUID(node.path("owner"));
        DateTime founded = new DateTime(node.path("founded").asLong());

        if (!ownerUUID.equals(owner.getUUID())) {
            throw new RuntimeException("Invalid owner!");
        }

        MemoryCity city = new MemoryCity(uuid, name, location, owner, founded, cityPublisher, cityFunction);

        for (Land land : lands) {
            city.addLand(land);
        }

        return city;
    }

    private Set<Land> parseLands(JsonNode node) throws IOException {
        THashSet<Land> lands = new THashSet<Land>();

        for (JsonNode landNode : node) {
            UUID uuid = toUUID(landNode.path("uuid"));
            UUID origin = toUUID(landNode.path("origin"));

            lands.add(new SimpleLand(uuid, origin));
        }

        return lands;
    }
}
