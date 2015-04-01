package org.societies;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;
import org.shank.service.AbstractService;
import org.shank.service.lifecycle.LifecycleContext;
import org.societies.api.Saveguard;

import java.io.File;

/**
 * Represents a SaveguardService
 */
public class SaveguardService extends AbstractService {

    private final Saveguard saveguard;
    private final File dataDirectory;
    private final String version;

    private final Logger logger;

    @Inject
    public SaveguardService(Saveguard saveguard,
                            @Named("data-directory") File dataDirectory,
                            @Named("version") String version, Logger logger) {
        this.saveguard = saveguard;
        this.dataDirectory = dataDirectory;
        this.version = version;
        this.logger = logger;
    }

    @Override
    public void start(LifecycleContext context) throws Exception {
        logger.info("Starting backup...");

        File backup = new File(dataDirectory, "backup");

        FileUtils.forceMkdir(backup);

        String[] backups = backup.list();

        boolean newBackup = true;

        for (String file : backups) {
            if (FilenameUtils.getBaseName(file).equalsIgnoreCase(version)) {
                newBackup = false;
            }
        }

        if (newBackup) {
            File file = new File(backup, version + ".zip");
            saveguard.backup(FileUtils.openOutputStream(file));
        }
    }
}
