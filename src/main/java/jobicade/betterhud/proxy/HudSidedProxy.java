package jobicade.betterhud.proxy;

import java.nio.file.Path;

import jobicade.betterhud.config.HudConfig;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

public interface HudSidedProxy {
    /**
     * @return The most recent version reported by the server.
     * @throws UnsupportedOperationException on the dedicated server.
     */
    default ArtifactVersion getServerVersion() {
        throw new UnsupportedOperationException();
    }

    /**
     * Updates the server version after a report is received.
     *
     * @param version The version or {@code null} to indicate no server.
     * @throws UnsupportedOperationException on the dedicated server.
     */
    default void setServerVersion(ArtifactVersion version) {
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
