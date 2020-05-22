package jobicade.betterhud.proxy;

import java.nio.file.Path;

import jobicade.betterhud.config.HudConfig;

public interface HudSidedProxy {
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
    default void registerReloadListeners() {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException on the dedicated server.
     */
    default void displayHudMenu() {
        throw new UnsupportedOperationException();
    }
}
