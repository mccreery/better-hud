package jobicade.betterhud.network;

import jobicade.betterhud.BetterHud;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.function.Supplier;

public class MessageVersion {
    public ArtifactVersion version;

    public MessageVersion() {}
    public MessageVersion(ArtifactVersion version2) {
        this.version = version2;
    }

    public MessageVersion(PacketBuffer packetBuffer) {
        // the no-arg readUtf() is @OnlyIn(Dist.Client)
        this(new DefaultArtifactVersion(packetBuffer.readUtf(32767)));
    }

    public void encode(PacketBuffer packetBuffer) {
        packetBuffer.writeUtf(version.toString());
    }

    public static void handle(MessageVersion message, Supplier<Context> context) {
        BetterHud.getLogger().info("Server reported version " + message.version);
        BetterHud.setServerVersion(message.version);
        context.get().setPacketHandled(true);
    }
}
