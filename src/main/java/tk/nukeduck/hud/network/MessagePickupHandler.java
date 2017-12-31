package tk.nukeduck.hud.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tk.nukeduck.hud.element.HudElement;

public class MessagePickupHandler implements IMessageHandler<MessagePickup, IMessage> {
	@Override
	public IMessage onMessage(MessagePickup message, MessageContext ctx) {
		HudElement.PICKUP.handler.pickupItem(message.getStack());
		return null;
	}
}
