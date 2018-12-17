package jobicade.betterhud.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

public class MessageVersion implements IMessage {
	public ArtifactVersion version;

	public MessageVersion() {}
	public MessageVersion(ArtifactVersion version2) {
		this.version = version2;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.version = new DefaultArtifactVersion(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, version.getVersionString());
	}
}
