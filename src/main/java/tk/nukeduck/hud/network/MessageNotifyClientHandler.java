package tk.nukeduck.hud.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tk.nukeduck.hud.BetterHud;

public class MessageNotifyClientHandler implements IMessageHandler<MessageNotifyClient, IMessage> {
	@Override
	public IMessage onMessage(MessageNotifyClient message, MessageContext ctx) {
		BetterHud.serverStatus.notifyPresence();
		return null;
	}
}
