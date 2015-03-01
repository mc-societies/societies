package org.societies.sieging.memory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.base.Function;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.inject.Inject;
import com.migcomponents.migbase64.Base64;
import gnu.trove.set.hash.THashSet;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.lib.core.uuid.UUIDStorage;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.societies.api.sieging.*;
import org.societies.bridge.Location;
import org.societies.bridge.World;
import org.societies.bridge.WorldResolver;
import org.societies.database.json.AbstractMapper;
import org.societies.groups.setting.Setting;
import org.societies.groups.setting.SettingProvider;
import org.societies.groups.setting.target.Target;

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

    private final WorldResolver worldResolver;
    private final UUIDStorage cityStorage;
    private final CityPublisher cityPublisher;
    private final BesiegerProvider besiegerProvider;

    private final Function<Integer, Double> cityFunction;

    @Inject
    public CityParser(Logger logger,
                      WorldResolver worldResolver,
                      @Named("cities") UUIDStorage cityStorage,
                      CityPublisher cityPublisher,
                      SettingProvider settingProvider,
                      BesiegerProvider besiegerProvider, @Named("city-function") Function<Integer, Double> cityFunction) {
        super(logger, settingProvider);
        this.worldResolver = worldResolver;
        this.cityStorage = cityStorage;
        this.cityPublisher = cityPublisher;
        this.besiegerProvider = besiegerProvider;
        this.cityFunction = cityFunction;
    }

    public Set<City> readBesieger(Besieger owner) throws IOException {
        File file = cityStorage.getFile(owner.getUUID());

        if (!file.exists()) {
            return Collections.emptySet();
        }

        JsonParser parser = createParser(file);
        parser.nextToken();
        Set<City> cities = readBesieger(parser, owner);

        parser.close();

        return cities;
    }

    public Set<City> readBesieger(JsonParser parser, Besieger empty) throws IOException {
        validateObject(parser);


        Set<City> cites = Collections.emptySet();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();

            parser.nextToken();

            if (fieldName.equals("unallocated")) {
                Set<UUID> uuids = parseLands(parser);
                for (UUID uuid : uuids) {
                    empty.addUnallocatedLand(new SpareLand(uuid));
                }
            } else if (fieldName.equals("cities")) {
                cites = readCities(parser, empty);
            }
        }


        return cites;
    }

    public Set<City> readCities(JsonParser parser, Besieger empty) throws IOException {
        if (parser.getCurrentToken() == null) {
            return Collections.emptySet();
        }

        THashSet<City> cities = new THashSet<City>();

        validateArray(parser);

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            City city = readCity(parser, empty);
            city.link();

            cities.add(city);
        }

        return cities;
    }

    public City readCity(JsonParser parser, Besieger owner) throws IOException {
        validateObject(parser);

        UUID uuid = null;
        String name = null;
        Location location = null;
        DateTime founded = null;
        Table<Setting, Target, String> settings = HashBasedTable.create();

        Set<UUID> lands = Collections.emptySet();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();

            parser.nextToken();

            if (fieldName.equals("uuid")) {
                uuid = UUIDGen.toUUID(Base64.decode(parser.getText()));
            } else if (fieldName.equals("name")) {
                name = parser.getText();
            } else if (fieldName.equals("location")) {
                validateObject(parser);

                World world = null;
                double x = 0, y = 0, z = 0;

                while (parser.nextToken() != JsonToken.END_OBJECT) {
                    fieldName = parser.getCurrentName();
                    parser.nextToken();

                    if (fieldName.equals("world")) {
                        world = worldResolver.getWorld(parser.getText());
                    } else if (fieldName.equals("x")) {
                        x = parser.getDoubleValue();
                    } else if (fieldName.equals("y")) {
                        y = parser.getDoubleValue();
                    } else if (fieldName.equals("z")) {
                        z = parser.getDoubleValue();
                    }

                    location = new Location(world, x, y, z);
                }

            } else if (fieldName.equals("lands")) {
                lands = parseLands(parser);
            } else if (fieldName.equals("owner")) {
                UUID ownerUUID = UUIDGen.toUUID(Base64.decode(parser.getText()));

                if (!ownerUUID.equals(owner.getUUID())) {
                    throw new RuntimeException("Invalid owner!");
                }
            } else if (fieldName.equals("founded")) {
                founded = new DateTime(parser.getLongValue());
            } else if (fieldName.equals("settings")) {
                readSettings(parser, settings);
            }
        }

        MemoryCity city = new MemoryCity(uuid, name, location, owner, founded, cityPublisher, cityFunction);

        Setting.set(settings, city, logger);

        for (UUID land : lands) {
            city.addLand(new SimpleLand(land, city));
        }

        return city;
    }

    private Set<UUID> parseLands(JsonParser parser) throws IOException {
        THashSet<UUID> lands = new THashSet<UUID>();

        String fieldName;
        validateArray(parser);

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            validateObject(parser);

            while (parser.nextToken() != JsonToken.END_OBJECT) {
                fieldName = parser.getCurrentName();

                parser.nextToken();

                if (fieldName.equals("uuid")) {
                    UUID land = UUIDGen.toUUID(Base64.decode(parser.getText()));

                    lands.add(land);
                }
            }
        }

        return lands;
    }
}
