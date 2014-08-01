package net.catharos.societies.database;


import net.catharos.lib.database.DSLProvider;
import net.catharos.lib.database.Database;
import net.catharos.lib.database.RemoteDatabase;
import net.catharos.lib.database.data.DataWorker;
import net.catharos.lib.database.data.queue.DefaultQueue;
import net.catharos.lib.database.data.queue.Queue;
import net.catharos.lib.shank.AbstractModule;
import org.jooq.SQLDialect;

import java.util.concurrent.TimeUnit;

/**
 * Represents a DatabaseModule
 */
public class DatabaseModule extends AbstractModule {

    @Override
    protected void configure() {
        bindNamedString(RemoteDatabase.DB_DATASOURCE_CLASS, "com.mysql.jdbc.jdbc2.optional.MysqlDataSource");

        bind(SQLDialect.class).toInstance(SQLDialect.MYSQL);
//
//        bind(SQLDialect.class).toInstance(SQLDialect.HSQLDB);
//        bindNamedString("db-url", "jdbc:hsqldb:mem:test");
//
//        bindNamedString(URLDatabase.DB_DATASOURCE_CLASS, "org.hsqldb.jdbc.JDBCDataSource");
//
//        bindNamedString("db-driver", "org.hsqldb.jdbc.JDBCDriver");

        bind(DataWorker.class);

        bind(Queue.class).to(DefaultQueue.class);
        bindNamedInstance("auto-flush-interval", long.class, 5000L);
        bindNamedInstance("max-batch-idle", long.class, 5000L);
        bindNamedInstance("queue-time-unit", TimeUnit.class, TimeUnit.MILLISECONDS);
        bindNamedInstance("critical-batch-size", int.class, 100);


        bind(Database.class).to(RemoteDatabase.class);
        bind(DSLProvider.class).to(RemoteDatabase.class);
    }
}
