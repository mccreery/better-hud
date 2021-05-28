package jobicade.betterhud.proxy;

import jobicade.betterhud.config.HudConfig;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.nio.file.Path;

public interface HudSidedProxy {
    /**
     * Called directly by the mod class during its pre-init.
     */
    default void preInit(FMLPreInitializationEvent event) {
    }

    /**
     * Called directly by the mod class during its init.
     */
    default void init(FMLInitializationEvent event) {
    }

    /**
     * @throws UnsupportedOperationException on the dedicated server.
     */
    default boolean isModEnabled() {
        throw new UnsupportedOperationException();
    }

    /**
     * @param configFile The path to the loaded configuration file.
     * @param configDirectory The path to the directory containing saves.
     * @throws UnsupportedOperationException on the dedicated server.
     */
    default void initConfigManager(Path configFile, Path configDirectory) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException on the dedicated server.
     */
    default HudConfig getConfig() {
        throw new UnsupportedOperationException();
    }
}
