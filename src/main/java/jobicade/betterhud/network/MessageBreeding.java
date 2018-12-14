package jobicade.betterhud.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageBreeding implements IMessage {
	public int entityId, inLove;

	public MessageBreeding() {}
	public MessageBreeding(int entityId, int inLove) {
		this.entityId = entityId;
		this.inLove = inLove;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.entityId = buf.readInt();
		this.inLove = buf.readShort();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityId);
		buf.writeShort(inLove);
	}
}
