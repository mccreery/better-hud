package jobicade.betterhud.config;

import com.google.common.io.MoreFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * A config slot representing an external config file.
 * This slot is a destination.
 */
public class FileConfigSlot implements ConfigSlot {
    private final Path path;
    private final String name;

    /**
     * Constructor for file config slots.
     * @param path The filesystem path for the config file.
     */
    public FileConfigSlot(Path path) {
        this.path = path;
        this.name = MoreFiles.getNameWithoutExtension(path);
    }

    @Override
    public void copyTo(Path dest) throws IOException {
        Files.copy(this.path, dest, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void copyFrom(Path source) throws IOException {
        Files.copy(source, this.path, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public boolean isDest() {
        return true;
    }

    @Override
    public String getName() {
        return name;
    }
}
