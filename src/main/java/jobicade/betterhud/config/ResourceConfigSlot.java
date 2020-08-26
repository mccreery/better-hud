package jobicade.betterhud.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

/**
 * A config slot representing a resource in the resource pack system.
 * This slot is not a destination.
 */
public class ResourceConfigSlot implements ConfigSlot {
    private final IResourceManager resourceManager;
    private final ResourceLocation resourceLocation;

    /**
     * Constructor for resource config slots.
     * @param path The resource location containing the config.
     */
    public ResourceConfigSlot(IResourceManager resourceManager, ResourceLocation resourceLocation) {
        this.resourceManager = resourceManager;
        this.resourceLocation = resourceLocation;
    }

    @Override
    public void copyTo(Path dest) throws IOException {
        try (IResource resource = resourceManager.getResource(resourceLocation)) {
            Files.copy(resource.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Override
    public void copyFrom(Path source) throws IOException {}

    @Override
    public boolean isDest() {
        return false;
    }

    @Override
    public String getName() {
        return com.google.common.io.Files.getNameWithoutExtension(resourceLocation.getPath());
    }
}
