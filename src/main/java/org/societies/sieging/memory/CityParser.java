package org.societies.sieging.memory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.inject.Inject;
import com.migcomponents.migbase64.Base64;
import gnu.trove.set.hash.THashSet;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.lib.core.uuid.UUIDStorage;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.City;
import org.societies.api.sieging.CityPublisher;
import org.societies.api.sieging.SimpleLand;
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
import java.util.UUID;

/**
 * Represents a CityMapper
 */
public class CityParser extends AbstractMapper {

    private final WorldResolver worldResolver;
    private final UUIDStorage cityStorage;
    private final CityPublisher cityPublisher;

    @Inject
    public CityParser(Logger logger,
                      WorldResolver worldResolver,
                      @Named("cities") UUIDStorage cityStorage,
                      CityPublisher cityPublisher,
                      SettingProvider settingProvider) {
        super(logger, settingProvider);
        this.worldResolver = worldResolver;
        this.cityStorage = cityStorage;
        this.cityPublisher = cityPublisher;
    }

    public void readCities(Besieger owner) throws IOException {
        File file = cityStorage.getFile(owner.getUUID());

        if (!file.exists()) {
            return;
        }

        JsonParser parser = createParser(file);

        readCities(parser, owner);

        parser.close();
    }

    public void readCities(JsonParser parser, Besieger owner) throws IOException {
        if (parser.getCurrentToken() == null) {
            return;
        }

        validateArray(parser);

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            City city = readCity(parser, owner);

            owner.addCity(city);
            city.link();
        }
    }

    public City readCity(JsonParser parser, Besieger owner) throws IOException {
        parser.nextToken();
        validateObject(parser);

        UUID uuid = null;
        String name = null;
        Location location = null;
        DateTime founded = null;
        Table<Setting, Target, String> settings = HashBasedTable.create();

        THashSet<UUID> lands = new THashSet<UUID>();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();

            parser.nextToken();

            if (fieldName.equals("uuid")) {
                uuid = UUIDGen.toUUID(Base64.decode(parser.getText()));
            } else if (fieldName.equals("name")) {
                name = parser.getText();
            } else if (fieldName.equals("location")) {
                validateObject(parser);

                while (parser.nextToken() != JsonToken.END_OBJECT) {
                    fieldName = parser.getCurrentName();
                    parser.nextToken();

                    World world = null;
                    double x = 0, y = 0, z = 0;

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

            } else if (fieldName.equals("owner")) {
                //todo
            } else if (fieldName.equals("founded")) {
                founded = new DateTime(parser.getLongValue());
            } else if (fieldName.equals("settings")) {
                readSettings(parser, settings);
            }
        }

        MemoryCity city = new MemoryCity(uuid, name, location, owner, founded, cityPublisher);

        Setting.set(settings, city, logger);

        for (UUID land : lands) {
            city.addLand(new SimpleLand(land, city));
        }

        return city;
    }
}
