package jobicade.betterhud.config;

import java.io.IOException;
import java.nio.file.Path;

public interface ConfigSlot {
    void copyTo(Path dest) throws IOException;
    void copyFrom(Path source) throws IOException;
    boolean isDest();

    String getName();

    default boolean matches(String name) {
        return name.equals(getName());
    }

    public static String getName(Path source) {
        String filename = source.getFileName().toString();
        int dot = filename.lastIndexOf('.');

        if(dot != -1) {
            return filename.substring(0, dot);
        } else {
            return filename;
        }
    }
}
