package jobicade.betterhud.proxy;

import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

public class ClientProxy implements HudSidedProxy {
    private ArtifactVersion serverVersion;

    public ClientProxy() {
        setServerVersion(null);
    }

    @Override
    public ArtifactVersion getServerVersion() {
        return serverVersion;
    }

    @Override
    public void setServerVersion(ArtifactVersion version) {
        if (version == null) {
            serverVersion = new DefaultArtifactVersion("version");
        } else {
            serverVersion = version;
        }
    }
}
