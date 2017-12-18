package tk.nukeduck.hud.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageNotifyClient implements IMessage {
	@Override
	public void fromBytes(ByteBuf buf) {
		//buf.readByte();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		//buf.writeByte(0);
	}
}
