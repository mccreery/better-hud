package jobicade.betterhud.proxy;

import net.minecraftforge.fml.common.versioning.ArtifactVersion;

public interface HudSidedProxy {
    /**
     * Supported on the client only.
     * @return The most recent version reported by the server.
     */
    default ArtifactVersion getServerVersion() {
        throw new UnsupportedOperationException();
    }

    /**
     * Supported on the client only.
     * Updates the server version after a report is received.
     */
    default void setServerVersion(ArtifactVersion version) {
        throw new UnsupportedOperationException();
    }
}
