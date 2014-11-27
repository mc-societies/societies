package net.catharos.societies.database.sql;


import com.google.inject.Inject;
import net.catharos.lib.database.Database;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.lib.shank.service.AbstractService;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;

/**
 * Represents a TablesService
 */
public class TablesService extends AbstractService {

    private final Database database;

    @InjectLogger
    private Logger logger;


    @Inject
    public TablesService(Database database) {this.database = database;}

    @Override
    public void init(Object context) throws Exception {
        logger.info("Generation database tables...");

        Flyway flyway = new Flyway();
        flyway.setDataSource(database.getDataSource());
        logger.info("Applied {0} migrations!", flyway.migrate());
    }
}
