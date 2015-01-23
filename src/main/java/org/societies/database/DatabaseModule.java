package org.societies.database;


import com.typesafe.config.Config;
import org.shank.AbstractModule;
import org.societies.database.json.JSONDatabaseModule;
import org.societies.database.sql.SQLDatabaseModule;
import org.societies.sql.SQLModule;

import java.io.File;

/**
 * Represents a DatabaseModule
 */
public class DatabaseModule extends AbstractModule {

    private final Config config;
    private final File dataDirectory;

    public DatabaseModule(Config config, File dataDirectory) {
        this.config = config;
        this.dataDirectory = dataDirectory;
    }

    @Override
    protected void configure() {
        String type = config.getString("database.type");
        if (type.equals("mysql")) {
            install(new SQLDatabaseModule());

            install(new SQLModule(config.getBoolean("database.mysql.caching")));
        } else if (type.equals("default")) {
            bindNamedInstance("group-root", File.class, new File(dataDirectory, config
                    .getString("database.default.societies")));
            bindNamedInstance("member-root", File.class, new File(dataDirectory, config
                    .getString("database.default.members")));

            install(new JSONDatabaseModule());
        }
    }
}
