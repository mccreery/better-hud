package jobicade.betterhud.network;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import net.minecraft.network.PacketBuffer;

public class MessageVersion {
    public ArtifactVersion version;

    public MessageVersion() {}
    public MessageVersion(ArtifactVersion version2) {
        this.version = version2;
    }

    public MessageVersion(PacketBuffer packetBuffer) {
        this(new DefaultArtifactVersion(packetBuffer.readString()));
    }

    public void encode(PacketBuffer packetBuffer) {
        packetBuffer.writeString(version.toString());
    }
}
