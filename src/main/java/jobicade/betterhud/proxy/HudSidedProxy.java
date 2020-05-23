package jobicade.betterhud.proxy;

import java.nio.file.Path;

import jobicade.betterhud.config.HudConfig;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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

    /**
     * @throws UnsupportedOperationException on the dedicated server.
     */
    default void setFirstTime() {
        throw new UnsupportedOperationException();
    }

    /**
     * Sends a message instructing the user how to use the configuration.
     * @throws UnsupportedOperationException on the dedicated server.
     */
    default void trySendTutorial(Entity entity) {
        throw new UnsupportedOperationException();
    }
}
