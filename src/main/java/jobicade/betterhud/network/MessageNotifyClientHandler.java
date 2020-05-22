package jobicade.betterhud.network;

import jobicade.betterhud.BetterHud;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageNotifyClientHandler implements IMessageHandler<MessageVersion, IMessage> {
	@Override
	public IMessage onMessage(MessageVersion message, MessageContext context) {
		BetterHud.getLogger().info("Server reported version " + message.version.getVersionString());

		BetterHud.getProxy().setServerVersion(message.version);
		return null;
	}
}
