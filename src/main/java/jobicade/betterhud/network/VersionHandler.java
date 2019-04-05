package jobicade.betterhud.network;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.util.function.Supplier;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import io.netty.buffer.ByteBufUtil;
import io.netty.util.CharsetUtil;
import jobicade.betterhud.BetterHud;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class VersionHandler {
	private static final CharsetDecoder decoder = CharsetUtil.decoder(CharsetUtil.UTF_8);

	public static void consume(ArtifactVersion version, Supplier<Context> context) {
		if(version != null) {
			BetterHud.serverVersion = version;
		}
	}

	public static void encode(ArtifactVersion version, PacketBuffer buf) {
		ByteBufUtil.writeUtf8(buf, version.toString());
	}

	public static ArtifactVersion decode(PacketBuffer buf) {
		try {
			String version = decoder.decode(buf.nioBuffer()).toString();
			return new DefaultArtifactVersion(version);
		} catch(CharacterCodingException e) {
			return null;
		}
	}
}
