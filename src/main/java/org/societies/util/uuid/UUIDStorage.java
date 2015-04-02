package org.societies.util.uuid;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Represents a UUIDStorage
 */
public class UUIDStorage implements Iterable<File> {

    private final File root;
    private final String extension;

    public UUIDStorage(File root, String extension) {
        this.root = root;
        this.extension = extension;
    }

    public void delete(UUID uuid) throws IOException {
        File file = getFile(uuid);

        FileUtils.deleteQuietly(file);

        File parent = file.getParentFile();

        while (parent != null) {
            if (!parent.isDirectory() || parent.list().length > 0) {
                break;
            }

            FileUtils.deleteDirectory(parent);
            parent = parent.getParentFile();
        }
    }

    public File getFile(UUID uuid) throws IOException {
        String name = uuid.toString();

        String first = name.substring(0, 2);
        String second = name.substring(2, 4);

        File file = new File(new File(root, first), second);

        FileUtils.forceMkdir(file);

        return new File(file, name + "." + extension);
    }

    @Override
    public Iterator<File> iterator() {
        if (!root.exists()) {
            return new Iterator<File>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public File next() {
                    throw new NoSuchElementException();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        return FileUtils.iterateFiles(root, new String[]{extension}, true);
    }
}
