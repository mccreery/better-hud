package jobicade.betterhud.config;

import static jobicade.betterhud.BetterHud.MC;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import net.minecraft.util.ResourceLocation;

public class ResourceConfigSlot implements ConfigSlot {
    private final ResourceLocation path;

    public ResourceConfigSlot(ResourceLocation path) {
        this.path = path;
    }

    @Override
    public void copyTo(Path dest) throws IOException {
        Files.copy(MC.getResourceManager().getResource(path).getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void copyFrom(Path source) throws IOException {}

    @Override
    public boolean isDest() {
        return false;
    }

    @Override
    public String getName() {
        return ConfigSlot.getName(Paths.get(path.getResourcePath()));
    }
}
