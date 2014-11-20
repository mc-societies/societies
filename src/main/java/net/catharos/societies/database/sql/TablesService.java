package net.catharos.societies.database.sql;


import com.google.inject.Inject;
import net.catharos.lib.database.Database;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.lib.shank.service.AbstractService;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

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
        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass()
                .getResourceAsStream("/tables.sql")));

        ScriptRunner scriptRunner = new ScriptRunner(database.getConnection(), false, true);
        scriptRunner.setLogWriter(new PrintWriter(new NullOutputStream()));
        scriptRunner.runScript(reader);
    }
}
