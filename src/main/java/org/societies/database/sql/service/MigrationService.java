package org.societies.database.sql.service;


import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.shank.logging.InjectLogger;
import org.shank.service.AbstractService;
import org.shank.service.lifecycle.LifecycleContext;
import org.societies.database.Database;

/**
 * Represents a TablesService
 */
public class MigrationService extends AbstractService {

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

        if (System.getProperty("clean-database") != null) {
            flyway.clean();
        }

        if (System.getProperty("baseline-database") != null) {
            flyway.setBaselineOnMigrate(true);
        }

        logger.info("Applied {0} migrations!", flyway.migrate());
    }
}
