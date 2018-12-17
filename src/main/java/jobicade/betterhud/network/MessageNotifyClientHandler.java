package jobicade.betterhud.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import jobicade.betterhud.BetterHud;

public class MessageNotifyClientHandler implements IMessageHandler<MessageVersion, IMessage> {
	@Override
	public IMessage onMessage(MessageVersion message, MessageContext context) {
		BetterHud.serverVersion = message.version;
		return null;
	}
}
