package jobicade.betterhud.network;

import jobicade.betterhud.registry.OverlayElements;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePickupHandler implements IMessageHandler<MessagePickup, IMessage> {
    @Override
    public IMessage onMessage(MessagePickup message, MessageContext ctx) {
        OverlayElements.PICKUP.refreshStack(message.getStack());
        return null;
    }
}
