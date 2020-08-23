package jobicade.betterhud.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import net.minecraft.resources.IResource;

/**
 * A config slot representing a resource in the resource pack system.
 * This slot is not a destination.
 */
public class ResourceConfigSlot implements ConfigSlot {
    private final IResource resource;

    /**
     * Constructor for resource config slots.
     * @param path The resource location containing the config.
     */
    public ResourceConfigSlot(IResource resource) {
        this.resource = resource;
    }

    @Override
    public void copyTo(Path dest) throws IOException {
        Files.copy(resource.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void copyFrom(Path source) throws IOException {}

    @Override
    public boolean isDest() {
        return false;
    }

    @Override
    public String getName() {
        return com.google.common.io.Files.getNameWithoutExtension(resource.getLocation().getPath());
    }
}
