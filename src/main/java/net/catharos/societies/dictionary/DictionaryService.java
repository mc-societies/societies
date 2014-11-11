package net.catharos.societies.dictionary;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.lib.core.i18n.MutableDictionary;
import net.catharos.lib.core.util.ZipUtils;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.lib.shank.service.AbstractService;
import net.catharos.lib.shank.service.lifecycle.LifecycleContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
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
    public DictionaryService(@Named("translations-url") URL translationsURL, MutableDictionary<String> dictionary, @Named("dictionary-directory") File directory) {
        this.translationsURL = translationsURL;
        this.dictionary = dictionary;
        this.directory = directory;
    }

    @Override
    public void init(LifecycleContext context) throws Exception {
        logger.info("Loading language files!");

        InputStream in;

        File localTranslations = new File(directory, "translations.zip");
        if (localTranslations.exists()) {
            in = new FileInputStream(localTranslations);
        } else {
            in = translationsURL.openStream();
        }

        in = new ByteArrayInputStream(ByteStreams.toByteArray(in));

        ZipInputStream zip = new ZipInputStream(in);

        final ArrayList<String> loaded = new ArrayList<String>();

        ZipUtils.listStreams(zip, "", new ZipUtils.Consumer() {
            @Override
            public void consume(String name, InputStream stream) {
                if (!name.endsWith("general.properties")) {
                    return;
                }
                try {
                    stream = new ByteArrayInputStream(ByteStreams.toByteArray(stream));

                    String lang = name.substring(0, name.length() - "general.properties".length() - 1);

                    InputStreamReader reader = new InputStreamReader(stream);

                    Properties properties = new Properties();
                    properties.load(new BufferedReader(reader));

                    File output = new File(directory, name);

//                    if (!output.exists()) {
//                        FileUtils.forceMkdir(output.getParentFile());
//                        output.createNewFile();
//                    }
//
//                    stream.reset();
//                    IOUtils.copy(stream, new FileOutputStream(output));

                    if (output.exists()) {
                        Properties current = new Properties();
                        current.load(new BufferedReader(new FileReader(output)));
                        properties.putAll(current);
                    }

                    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                        dictionary.addTranslation(lang, entry.getKey().toString(), entry.getValue().toString());
                    }

                    loaded.add(lang);

                } catch (IOException e) {
                    logger.catching(e);
                }

            }
        });

        File cache = new File(directory, ".cache-translations.zip");
        in.reset();

        IOUtils.copy(in, FileUtils.openOutputStream(cache));

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
