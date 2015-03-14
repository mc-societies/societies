package org.societies;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;
import org.shank.logging.InjectLogger;
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

    @InjectLogger
    private Logger logger;

    @Inject
    public SaveguardService(Saveguard saveguard,
                            @Named("data-directory") File dataDirectory,
                            @Named("version") String version) {
        this.saveguard = saveguard;
        this.dataDirectory = dataDirectory;
        this.version = version;
    }

    @Override
    public void start(LifecycleContext context) throws Exception {
        logger.info("Starting backup...");

        File backup = new File(dataDirectory, "backup");

        FileUtils.forceMkdir(backup);

        String[] backups = backup.list();

        boolean newBackup = true;

        for (String file : backups) {
            if (versionCompare(FilenameUtils.getBaseName(file), version) == 0) {
                newBackup = false;
            }
        }

        if (newBackup) {
            File file = new File(backup, version + ".zip");
            saveguard.backup(FileUtils.openOutputStream(file));
        }
    }

    public int versionCompare(String str1, String str2)
    {
        String[] vals1 = str1.split("\\.");
        String[] vals2 = str2.split("\\.");
        int i = 0;
        // set index to first non-equal ordinal or length of shortest version string
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i]))
        {
            i++;
        }
        // compare first non-equal ordinal number
        if (i < vals1.length && i < vals2.length)
        {
            int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return Integer.signum(diff);
        }
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        else
        {
            return Integer.signum(vals1.length - vals2.length);
        }
    }
}
