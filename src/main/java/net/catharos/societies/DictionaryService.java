package net.catharos.societies;

import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.lib.core.i18n.MutableDictionary;
import net.catharos.lib.core.util.ExtensionFilter;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.lib.shank.service.AbstractService;
import net.catharos.lib.shank.service.lifecycle.LifecycleContext;
import org.apache.commons.lang.LocaleUtils;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Represents a DictionaryService
 */
public class DictionaryService extends AbstractService {

    private final MutableDictionary<String> dictionary;
    private final File directory;

    @InjectLogger
    private Logger logger;

    @Inject
    public DictionaryService(MutableDictionary<String> dictionary, @Named("dictionary-directory") File directory) {
        this.dictionary = dictionary;
        this.directory = directory;
    }

    @Override
    public void init(LifecycleContext context) throws Exception {
        logger.info("Loading language files!");

        try {
            for (File file : directory.listFiles(new ExtensionFilter("properties"))) {
                String name = Files.getNameWithoutExtension(file.getName());
                Locale locale = LocaleUtils.toLocale(name);

                Properties properties = new Properties();

                properties.load(new FileInputStream(file));

                for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                    dictionary.addTranslation(locale, entry.getKey().toString(), entry.getValue().toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
