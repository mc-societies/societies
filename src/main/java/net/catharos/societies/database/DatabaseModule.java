package net.catharos.societies.database;


import net.catharos.lib.database.DSLProvider;
import net.catharos.lib.database.Database;
import net.catharos.lib.database.data.DataWorker;
import net.catharos.lib.shank.AbstractModule;

/**
 * Represents a DatabaseModule
 */
public class DatabaseModule extends AbstractModule {

    private final String host, database, username, password;
    private final int port;

    public DatabaseModule(String host,
                          String database,
                          String username,
                          String password,
                          int port) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    @Override
    protected void configure() {
        bindNamedString(Database.DB_HOST_KEY, host);
        bindNamedInstance(Database.DB_PORT_KEY, int.class, port);
        bindNamedString(Database.DB_DATABASE_KEY, database);
        bindNamedString(Database.DB_USERNAME_KEY, username);
        bindNamedString(Database.DB_PASSWORD_KEY, password);

        bind(DataWorker.class);

        bind(DSLProvider.class).to(Database.class);
    }
}
