package jobicade.betterhud.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;

/**
 * User-defined config resource locations with a default config.
 */
public class ConfigResourceList {
    private final List<ResourceLocation> configs = new ArrayList<>();
    private ResourceLocation defaultConfig;

    public void combine(ConfigResourceList list) {
        configs.addAll(list.configs);

        if (list.defaultConfig != null) {
            defaultConfig = list.defaultConfig;
        }
    }

    public List<ResourceLocation> getConfigs() {
        return configs;
    }

    public ResourceLocation getDefaultConfig() {
        return defaultConfig;
    }
}
