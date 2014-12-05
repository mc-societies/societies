package org.societies.util;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;

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
        destination.mkdirs();
        File cacheFile = new File(destination, ".cache");

        boolean download = true;

        if (cacheFile.exists()) {
            String source = CharStreams.toString(new FileReader(cacheFile));
            if (source.equals(url.toExternalForm())) {
                download = false;
            }
        }

        if (download) {
            logger.info("Downloading dependencies...");
            Set<String> loaded = download(destination, url);
            PrintWriter printWriter = new PrintWriter(cacheFile);
            printWriter.print(url.toExternalForm());
            printWriter.flush();
            printWriter.close();

            File[] dependencies = destination.listFiles(new JarFilter());

            for (File dependency : dependencies) {
                if (!loaded.contains(dependency.getName())) {
                    logger.info("Deleting " + dependency.getName() + "...");
                    dependency.delete();
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

                file.getParentFile().mkdirs();

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
