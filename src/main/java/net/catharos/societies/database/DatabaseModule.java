package net.catharos.societies.database;


import net.catharos.lib.database.DSLProvider;
import net.catharos.lib.database.jbdc.RemoteDatabase;
import net.catharos.lib.database.data.DataWorker;
import net.catharos.lib.shank.AbstractModule;
import org.jooq.SQLDialect;

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
        bindNamedString(RemoteDatabase.DB_HOST_KEY, host);
        bindNamedInstance(RemoteDatabase.DB_PORT_KEY, int.class, port);
        bindNamedString(RemoteDatabase.DB_DATABASE_KEY, database);
        bindNamedString(RemoteDatabase.DB_USERNAME_KEY, username);
        bindNamedString(RemoteDatabase.DB_PASSWORD_KEY, password);

        bindNamedString(RemoteDatabase.DB_DATASOURCE_CLASS, "com.mysql.jdbc.jdbc2.optional.MysqlDataSource");

        bind(SQLDialect.class).toInstance(SQLDialect.MYSQL);

        bind(DataWorker.class);

        bind(DSLProvider.class).to(RemoteDatabase.class);
    }
}
