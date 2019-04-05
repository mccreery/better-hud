package jobicade.betterhud.network;

import javax.xml.ws.handler.MessageContext;

import jobicade.betterhud.BetterHud;

public class MessageNotifyClientHandler implements IMessageHandler<MessageVersion, IMessage> {
	@Override
	public IMessage onMessage(MessageVersion message, MessageContext context) {
		BetterHud.serverVersion = message.version;
		return null;
	}
}
