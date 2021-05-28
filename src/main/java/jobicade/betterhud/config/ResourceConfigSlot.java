package jobicade.betterhud.config;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * A config slot representing a resource in the resource pack system.
 * This slot is not a destination.
 */
public class ResourceConfigSlot implements ConfigSlot {
    private final ResourceLocation path;

    /**
     * Constructor for resource config slots.
     * @param path The resource location containing the config.
     */
    public ResourceConfigSlot(ResourceLocation path) {
        this.path = path;
    }

    @Override
    public void copyTo(Path dest) throws IOException {
        Files.copy(Minecraft.getInstance().getResourceManager().getResource(path).getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void copyFrom(Path source) throws IOException {}

    @Override
    public boolean isDest() {
        return false;
    }

    @Override
    public String getName() {
        return com.google.common.io.Files.getNameWithoutExtension(path.getPath());
    }
}
