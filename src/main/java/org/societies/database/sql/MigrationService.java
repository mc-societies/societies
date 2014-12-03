package org.societies.database.sql;


import com.google.inject.Inject;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.lib.shank.service.AbstractService;
import net.catharos.lib.shank.service.lifecycle.LifecycleContext;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.societies.database.Database;

/**
 * Represents a TablesService
 */
class MigrationService extends AbstractService {

    private final Database database;
    private final ClassLoader classLoader;

    @InjectLogger
    private Logger logger;

    @Inject
    public MigrationService(Database database, ClassLoader classLoader) {
        this.database = database;
        this.classLoader = classLoader;
    }

    @Override
    public void init(LifecycleContext context) throws Exception {
        try {
            database.initDatabase();
        } catch (RuntimeException e) {
            logger.fatal("Failed to connect to database! {0}", e.getMessage());
            return;
        }
        logger.info("Generation database tables...");

        Flyway flyway = new Flyway();
        flyway.setBaselineOnMigrate(true);
        flyway.setClassLoader(classLoader);
        flyway.setDataSource(database.getDataSource());
        logger.info("Applied {0} migrations!", flyway.migrate());
    }
}
