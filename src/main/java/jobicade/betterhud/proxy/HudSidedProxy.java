package jobicade.betterhud.proxy;

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
}
