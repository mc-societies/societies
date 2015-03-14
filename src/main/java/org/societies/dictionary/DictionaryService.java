package org.societies.dictionary;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.lib.core.i18n.MutableDictionary;
import net.catharos.lib.core.util.ZipUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.shank.logging.InjectLogger;
import org.shank.service.AbstractService;
import org.shank.service.lifecycle.LifecycleContext;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
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
    public DictionaryService(@Named("translations-url") URL translationsURL,
                             MutableDictionary<String> dictionary,
                             @Named("dictionary-directory") File directory) {
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
            try {
                in = translationsURL.openStream();
            } catch (IOException ignored) {
                File cached = getCachedTranslations();

                if (cached.exists()) {
                    logger.info("Loading cached translations!");
                    in = new FileInputStream(cached);
                } else {
                    try {
                        logger.info("Loading translations from crowdin!");
                        URLConnection conn = new URL("http://crowdin.com/download/project/societies.zip").openConnection();

                        String redirect = conn.getHeaderField("Location");

                        if (redirect != null){
                            conn = new URL(redirect).openConnection();
                        }

                        in = conn.getInputStream();
                    } catch (IOException e) {
                        logger.warn("Failed to download translations!");
                        return;
                    }
                }
            }
        }



        final List<String> loaded = loadZip(in);

        logger.info("Loaded the following languages: " + loaded.toString());

        in.close();
    }

    public List<String> loadZip(InputStream in) throws IOException {
        byte[] buffer = ByteStreams.toByteArray(in);
        in = new ByteArrayInputStream(buffer);
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

                    InputStreamReader reader = new InputStreamReader(stream, "UTF-8");

                    Properties properties = new Properties();
                    properties.load(new BufferedReader(reader));

                    File output = new File(directory, name);

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

        in.reset();

        if (in.available() > 0) {
            FileOutputStream cached = FileUtils.openOutputStream(getCachedTranslations());
            IOUtils.copy(in, cached);
            cached.close();
        }

        return loaded;
    }

    private File getCachedTranslations() {
        return new File(directory, ".cache-translations.zip");
    }

}
