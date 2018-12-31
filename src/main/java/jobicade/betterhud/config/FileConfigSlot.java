package jobicade.betterhud.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileConfigSlot implements ConfigSlot {
    private final Path path;
    private final String name;

    public FileConfigSlot(Path path) {
        this.path = path;
        this.name = ConfigSlot.getName(path);
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
