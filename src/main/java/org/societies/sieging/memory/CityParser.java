package org.societies.sieging.memory;

import com.google.inject.Inject;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.City;
import org.societies.bridge.WorldResolver;
import org.societies.database.json.AbstractMapper;

import java.util.Set;

/**
 * Represents a CityMapper
 */
public class CityParser extends AbstractMapper {

    @Inject
    public CityParser(WorldResolver worldResolver) {
        super(worldResolver);
    }

    public Set<City> readBesieger(Besieger besieger) {
        return null;
    }

//    private final WorldResolver worldResolver;
//    private final UUIDStorage cityStorage;
//    private final CityPublisher cityPublisher;
//
//    private final Function<Integer, Double> cityFunction;
//
//    @Inject
//    public CityParser(Logger logger,
//                      WorldResolver worldResolver,
//                      @Named("cities") UUIDStorage cityStorage,
//                      CityPublisher cityPublisher,
//                      @Named("city-function") Function<Integer, Double> cityFunction) {
//        this.worldResolver = worldResolver;
//        this.cityStorage = cityStorage;
//        this.cityPublisher = cityPublisher;
//        this.cityFunction = cityFunction;
//    }
//
//    public Set<City> readBesieger(Besieger owner) throws IOException {
//        File file = cityStorage.getFile(owner.getUUID());
//
//        if (!file.exists()) {
//            return Collections.emptySet();
//        }
//
//        JsonParser parser = createNode(file);
//        parser.nextToken();
//        Set<City> cities = readBesieger(parser, owner);
//
//        parser.close();
//
//        return cities;
//    }
//
//    public Set<City> readBesieger(JsonParser parser, Besieger empty) throws IOException {
//        validateObject(parser);
//
//
//        Set<City> cites = Collections.emptySet();
//
//        while (parser.nextToken() != JsonToken.END_OBJECT) {
//            String fieldName = parser.getCurrentName();
//
//            parser.nextToken();
//
//            if (fieldName.equals("unallocated")) {
//                Set<Land> lands = parseLands(parser);
//                for (Land land : lands) {
//                    empty.addUnallocatedLand(land);
//                }
//            } else if (fieldName.equals("cities")) {
//                cites = readCities(parser, empty);
//            }
//        }
//
//
//        return cites;
//    }
//
//    public Set<City> readCities(JsonParser parser, Besieger empty) throws IOException {
//        if (parser.getCurrentToken() == null) {
//            return Collections.emptySet();
//        }
//
//        THashSet<City> cities = new THashSet<City>();
//
//        validateArray(parser);
//
//        while (parser.nextToken() != JsonToken.END_ARRAY) {
//            City city = readCity(parser, empty);
//            city.link();
//
//            cities.add(city);
//        }
//
//        return cities;
//    }
//
//    public City readCity(JsonParser parser, Besieger owner) throws IOException {
//        validateObject(parser);
//
//        UUID uuid = null;
//        String name = null;
//        Location location = null;
//        DateTime founded = null;
//
//        Set<Land> lands = Collections.emptySet();
//
//        while (parser.nextToken() != JsonToken.END_OBJECT) {
//            String fieldName = parser.getCurrentName();
//
//            parser.nextToken();
//
//            if (fieldName.equals("uuid")) {
//                uuid = UUIDGen.toUUID(Base64.decode(parser.getText()));
//            } else if (fieldName.equals("name")) {
//                name = parser.getText();
//            } else if (fieldName.equals("location")) {
//                validateObject(parser);
//
//                World world = null;
//                double x = 0, y = 0, z = 0;
//
//                while (parser.nextToken() != JsonToken.END_OBJECT) {
//                    fieldName = parser.getCurrentName();
//                    parser.nextToken();
//
//                    if (fieldName.equals("world")) {
//                        world = worldResolver.getWorld(parser.getText());
//                    } else if (fieldName.equals("x")) {
//                        x = parser.getDoubleValue();
//                    } else if (fieldName.equals("y")) {
//                        y = parser.getDoubleValue();
//                    } else if (fieldName.equals("z")) {
//                        z = parser.getDoubleValue();
//                    }
//
//                    location = new Location(world, x, y, z);
//                }
//
//            } else if (fieldName.equals("lands")) {
//                lands = parseLands(parser);
//            } else if (fieldName.equals("owner")) {
//                UUID ownerUUID = UUIDGen.toUUID(Base64.decode(parser.getText()));
//
//                if (!ownerUUID.equals(owner.getUUID())) {
//                    throw new RuntimeException("Invalid owner!");
//                }
//            } else if (fieldName.equals("founded")) {
//                founded = new DateTime(parser.getLongValue());
//            }
//        }
//
//        MemoryCity city = new MemoryCity(uuid, name, location, owner, founded, cityPublisher, cityFunction);
//
//        for (Land land : lands) {
//            city.addLand(land);
//        }
//
//        return city;
//    }
//
//    private Set<Land> parseLands(JsonParser parser) throws IOException {
//        THashSet<Land> lands = new THashSet<Land>();
//
//        String fieldName;
//        validateArray(parser);
//
//        while (parser.nextToken() != JsonToken.END_ARRAY) {
//            validateObject(parser);
//
//            while (parser.nextToken() != JsonToken.END_OBJECT) {
//                fieldName = parser.getCurrentName();
//
//                parser.nextToken();
//
//                UUID uuid = null, origin = null;
//
//                if (fieldName.equals("uuid")) {
//                    uuid = UUIDGen.toUUID(Base64.decode(parser.getText()));
//                } else if (fieldName.equals("origin")) {
//                    origin = UUIDGen.toUUID(Base64.decode(parser.getText()));
//                }
//
//                lands.add(new SimpleLand(uuid, origin));
//            }
//        }
//
//        return lands;
//    }
}
