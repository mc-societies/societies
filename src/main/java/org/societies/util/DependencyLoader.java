package org.societies.util;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Represents a DependencyLoader
 */
public class DependencyLoader {

    private final Logger logger;

    public DependencyLoader(Logger logger) {this.logger = logger;}

    public void loadDependencies(URL url, File destination) throws InvocationTargetException, IllegalAccessException, IOException {
        FileUtils.forceMkdir(destination);
        File cacheFile = new File(destination, ".cache");

        boolean download = true;

        if (cacheFile.exists()) {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(cacheFile), "UTF-8");
            try {
                String source = CharStreams.toString(reader);
                if (source.equals(url.toExternalForm())) {
                    download = false;
                }
            } finally {
                reader.close();
            }
        }

        if (download) {
            logger.info("Downloading dependencies...");
            Set<String> loaded = download(destination, url);
            PrintWriter printWriter = new PrintWriter(cacheFile, "UTF-8");
            try {
                printWriter.print(url.toExternalForm());
                printWriter.flush();
            } finally {
                printWriter.close();
            }

            File[] dependencies = destination.listFiles(new JarFilter());

            for (File dependency : dependencies) {
                if (!loaded.contains(dependency.getName())) {
                    logger.info("Deleting " + dependency.getName() + "...");
                    FileUtils.deleteQuietly(dependency);
                }
            }
        }

        logger.info("Loading dependencies...");
        loadDependencies(destination);
    }

    public void loadDependencies(File directory) throws InvocationTargetException, IllegalAccessException, IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();


        if (!(classLoader instanceof URLClassLoader)) {
            return;
        }

        URLClassLoader urlClassLoader = (URLClassLoader) classLoader;

        File[] dependencies = directory.listFiles(new JarFilter());

        for (File file : dependencies) {
            addFile(urlClassLoader, file);
        }
    }

    public boolean isLoadable(String name) {
        return name.endsWith(".jar") || name.endsWith(".zip");
    }

    public Set<String> download(File destination, URL url) throws IOException {
        HashSet<String> loaded = new HashSet<String>();
        InputStream is = url.openStream();

        ZipInputStream zipStream = new ZipInputStream(new BufferedInputStream(is));

        ZipEntry entry;

        while ((entry = zipStream.getNextEntry()) != null) {
            if (isLoadable(entry.getName())) {
                File file = new File(destination, entry.getName());

                FileUtils.forceMkdir(file.getParentFile());

                loaded.add(entry.getName());

                if (file.exists() && file.length() == entry.getSize()) {
                    logger.info("Skipping " + entry.getName() + "!");
                    continue;
                }

                logger.info("Downloading " + entry.getName() + "...");

                ByteStreams.copy(zipStream, new FileOutputStream(file));
            }
        }

        zipStream.close();
        return loaded;
    }


    private void addFile(URLClassLoader urlClassLoader, File file) throws IOException, IllegalAccessException, InvocationTargetException {
        addURL(urlClassLoader, file.toURI().toURL());
    }

    private Method addMethod;

    {
        try {
            addMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void addURL(URLClassLoader urlClassLoader, URL u) throws IOException, InvocationTargetException, IllegalAccessException {
        addMethod.invoke(urlClassLoader, u);
    }

    private class JarFilter implements FileFilter {
        @Override
        public boolean accept(File file) {
            return isLoadable(file.getName());
        }
    }
}
