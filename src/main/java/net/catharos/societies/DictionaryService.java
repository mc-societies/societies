package net.catharos.societies;

import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.typesafe.config.*;
import net.catharos.lib.core.i18n.MutableDictionary;
import net.catharos.lib.core.util.JarUtils;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.lib.shank.service.AbstractService;
import net.catharos.lib.shank.service.lifecycle.LifecycleContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.LocaleUtils;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarFile;

/**
 * Represents a DictionaryService
 */
class DictionaryService extends AbstractService {

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

        JarFile jar = JarUtils.getJarFile();

        Map<String, InputStream> languages = JarUtils.listStreams(jar, "defaults/languages");

        for (Map.Entry<String, InputStream> entry : languages.entrySet()) {
            String file = entry.getKey();
            String name = Files.getNameWithoutExtension(file);
            Locale locale = LocaleUtils.toLocale(name);

            logger.info("Loading language: %s", name);

            InputStreamReader reader = new InputStreamReader(entry.getValue());
            ConfigParseOptions options = ConfigParseOptions.defaults().setSyntax(ConfigSyntax.PROPERTIES);
            Config defaultConfig = ConfigFactory.parseReader(new BufferedReader(reader), options);
            reader.close();

            File output = new File(directory, file);
            Config config = ConfigFactory.parseFile(output).withFallback(defaultConfig);

            for (Map.Entry<String, ConfigValue> langEntry : config.entrySet()) {
                dictionary.addTranslation(locale, langEntry.getKey(), langEntry.getValue().unwrapped().toString());
            }

            FileUtils.writeStringToFile(output, config.root().render(ConfigRenderOptions.defaults().setOriginComments(false).setJson(false)));
        }

        jar.close();
    }
}
