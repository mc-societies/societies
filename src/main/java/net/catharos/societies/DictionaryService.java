package net.catharos.societies;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.typesafe.config.*;
import net.catharos.lib.core.i18n.MutableDictionary;
import net.catharos.lib.core.util.ZipUtils;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.lib.shank.service.AbstractService;
import net.catharos.lib.shank.service.lifecycle.LifecycleContext;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * Represents a DictionaryService
 */
class DictionaryService extends AbstractService {


    private final URL translationsURL;
    private final MutableDictionary<String> dictionary;
    private final File directory;

    @InjectLogger
    private Logger logger;

    @Inject
    public DictionaryService(@Named("translation-url") URL translationsURL, MutableDictionary<String> dictionary, @Named("dictionary-directory") File directory) {
        this.translationsURL = translationsURL;
        this.dictionary = dictionary;
        this.directory = directory;
    }

    @Override
    public void init(LifecycleContext context) throws Exception {
        logger.info("Loading language files!");

        ZipInputStream zip = new ZipInputStream(translationsURL.openStream());

        org.apache.commons.io.IOUtils.copy(translationsURL.openStream(), new FileOutputStream("/tmp/lang.zip"));

        final ArrayList<String> loaded = new ArrayList<String>();

        Map<String, InputStream> languages = ZipUtils.listStreams(zip, "", new ZipUtils.Consumer() {
            @Override
            public void consume(String name, InputStream stream) {
//                String key = entry.getKey();
                if (!name.endsWith("general.properties")) {
                    return;
                }

                name = name.substring(0, name.length() - "general.properties".length() - 1);

                InputStreamReader reader = new InputStreamReader(stream);
                ConfigParseOptions options = ConfigParseOptions.defaults().setSyntax(ConfigSyntax.PROPERTIES);
                Config defaultConfig = ConfigFactory.parseReader(new BufferedReader(reader), options);

                File output = new File(directory, name);
                Config config = ConfigFactory.parseFile(output).withFallback(defaultConfig);

                for (Map.Entry<String, ConfigValue> langEntry : config.entrySet()) {
                    dictionary.addTranslation(name, langEntry.getKey(), langEntry.getValue().unwrapped().toString());
                }

                loaded.add(name);
            }
        });

        logger.info("Loaded the following languages: " + loaded.toString());

        zip.close();
    }

////    @Override
//    public void init0(LifecycleContext context) throws Exception {
//        logger.info("Loading language files!");
//
//        JarFile jar = JarUtils.getJarFile();
//
//        Map<String, InputStream> languages = JarUtils.listStreams(jar, "defaults/languages");
//
//        for (Map.Entry<String, InputStream> entry : languages.entrySet()) {
//            String file = entry.getKey();
//            String name = Files.getNameWithoutExtension(file);
//            Locale locale = LocaleUtils.toLocale(name);
//
//            logger.info("Loading language: %s", name);
//
//            Reader reader = new BufferedReader(new InputStreamReader(entry.getValue()));
//
//            Properties defaultConfig = new Properties();
//            defaultConfig.load(reader);
//
//            reader.close();
//
//            File output = new File(directory, file);
//            Properties config = new Properties();
//            if (output.exists()) {
//                config.load(reader);
//            }
//
//            defaultConfig.putAll(config);
//            config = defaultConfig;
//
//
//            for (Map.Entry<Object, Object> langEntry : config.entrySet()) {
//                dictionary.addTranslation(locale, langEntry.getKey().toString(), langEntry.getValue().toString());
//            }
//
//            if (!output.exists()) {
//                output.getParentFile().mkdirs();
//                output.createNewFile();
//            }
//
//            FileOutputStream outputStream = new FileOutputStream(output);
//            config.store(outputStream, "");
//            outputStream.close();
//        }
//
//        jar.close();
//    }
}
