package jobicade.betterhud.config;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Represents a location from which a config file can be loaded or saved.
 * Some slots are read-only, such as resources, whereas some support writing
 * and are called "destinations."
 */
public interface ConfigSlot {
    /**
     * Copies the config in this slot to the given file path.
     * This is analagous to loading the contents into the file.
     *
     * @param dest The destination file path.
     */
    void copyTo(Path dest) throws IOException;

    /**
     * Copies the file at the given path into this slot.
     * This is analagous to saving the file.
     * Only call this method after checking {@link #isDest()}, as only
     * destination slots support this operation.
     *
     * @param source The source file path.
     * @see #isDest()
     */
    default void copyFrom(Path source) throws IOException {
        throw new UnsupportedOperationException();
    };

    /**
     * Returns {@code true} for slots which support the {@link #copyFrom(Path)}
     * operation, and {@code false} for those which do not.
     *
     * @return {@code true} for destination slots, {@code false} for others.
     * @see #copyFrom(Path)
     */
    boolean isDest();

    /**
     * Getter for the human-friendly name for this slot. One example is
     * the basename of a file.
     *
     * @return A human-friendly name for this slot.
     */
    String getName();

    /**
     * Checks whether an input name should highlight this entry in the list.
     * By default, this compares the two names.
     *
     * @return {@code true} if an input name matches this slot.
     */
    default boolean matches(String name) {
        return name.equals(getName());
    }
}
