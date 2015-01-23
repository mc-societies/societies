package org.societies.database.sql;

import org.jooq.SQLDialect;
import org.shank.service.AbstractServiceModule;
import org.shank.service.AbstractServicePrivateModule;
import org.societies.database.DSLProvider;
import org.societies.database.Database;
import org.societies.database.RemoteDatabase;
import org.societies.database.data.queue.DefaultQueue;
import org.societies.database.data.queue.Queue;

import java.util.concurrent.TimeUnit;

/**
 * Represents a MemberProviderModule
 */
public class SQLDatabaseModule extends AbstractServiceModule {

    @Override
    protected void configure() {
        bindService().to(MigrationService.class);

        install(new AbstractServicePrivateModule() {
            @Override
            protected void configure() {
                bind(CleanupService.class);
                bindService().to(CleanupService.class);

                expose(CleanupService.class);
            }
        });


        bind(Database.class).to(RemoteDatabase.class);
        bind(DSLProvider.class).to(RemoteDatabase.class);
        bindNamedString(RemoteDatabase.DB_DATASOURCE_CLASS, "com.mysql.jdbc.jdbc2.optional.MysqlDataSource");

        bind(SQLDialect.class).toInstance(SQLDialect.MYSQL);


//        bind(Database.class).to(URLDatabase.class);
//        bind(DSLProvider.class).to(URLDatabase.class);
//        bindNamedString(RemoteDatabase.DB_DATASOURCE_CLASS, "org.h2.jdbcx.JdbcDataSource");
//        bindNamedString("db-url", "jdbc:h2:./test");
//        bindNamedString("db-driver", "org.h2.Driver");
//
//        bind(SQLDialect.class).toInstance(SQLDialect.H2);


        bind(Queue.class).to(DefaultQueue.class);
        bindNamedInstance("auto-flush-interval", long.class, 5000L);
        bindNamedInstance("max-batch-idle", long.class, 5000L);
        bindNamedInstance("queue-time-unit", TimeUnit.class, TimeUnit.MILLISECONDS);
        bindNamedInstance("critical-batch-size", int.class, 100);
    }
}
