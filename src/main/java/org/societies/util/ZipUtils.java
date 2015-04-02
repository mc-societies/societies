package org.societies.util;

import gnu.trove.map.hash.THashMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Represents a JarUtils
 */
public class ZipUtils {

    public static void extract(String folder, File target) throws IOException, URISyntaxException {
        URL source = ZipUtils.class.getProtectionDomain().getCodeSource().getLocation();
        File jarFile = new File(source.getPath());


        if (jarFile.isFile()) {
            extractJar(source, target, folder);
        } else {
            URL url = ZipUtils.class.getResource("/" + folder);
            if (url != null) {
                File file = new File(url.toURI());

                FileUtils.copyDirectory(file, target);
            }
        }
    }

    public static void extractJar(URL source, File target, String folder) throws IOException {
        int offset = folder.length() + 1;

        ZipFile jar = new ZipFile(new File(source.getPath()));
        Enumeration entries = jar.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();

            String name = entry.getName();
            if (!name.startsWith(folder + "/") || name.length() <= offset) {
                continue;
            }

            File file = new File(target, name.substring(offset));

            if (file.exists()) {
                continue;
            }

            if (entry.isDirectory()) { // if its a directory, create it
                if (!file.mkdirs()) {
                    throw new IOException("Failed to create directory!");
                }
                continue;
            } else {
                if (file.getParentFile().mkdirs()) {
                    if (!file.createNewFile()) {
                        continue;
                    }
                }
            }

            InputStream is = jar.getInputStream(entry);
            FileOutputStream out = new FileOutputStream(file);
            IOUtils.copy(is, out);

            out.close();
            is.close();
        }

        jar.close();
    }

//    public static Map<String, InputStream> listStreams(String folder) throws IOException {
//        URL source = JarUtils.class.getProtectionDomain().getCodeSource().getLocation();
//        return listStreams(source, folder);
//    }

    public static ZipFile getJarFile() throws IOException {
        return new ZipFile(ZipUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }

    public static Map<String, InputStream> listStreams(ZipInputStream jar, String folder, Consumer filter) throws IOException {

        THashMap<String, InputStream> output = new THashMap<String, InputStream>();

        int offset = folder.length() + 1;

//        Enumeration entries = jar.entries();

        ZipEntry entry;
        while ((entry = jar.getNextEntry()) != null) {
//            ZipEntry entry = (ZipEntry) entries.nextElement();

            String name = entry.getName();
            if (name.length() <= offset || name.endsWith("/")) {
                continue;
            }
//                                 entry.
            filter.consume(name, jar);
        }

        return output;
    }

    public interface Consumer {

        void consume(String name, InputStream stream);
    }
}
