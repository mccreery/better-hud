package jobicade.betterhud.network;

import jobicade.betterhud.element.HudElement;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MessagePickupHandler implements BiConsumer<MessagePickup, Supplier<NetworkEvent.Context>> {
    @Override
    public void accept(MessagePickup message, Supplier<Context> contextSupplier) {
        HudElement.PICKUP.refreshStack(message.getStack());
        contextSupplier.get().setPacketHandled(true);
    }
}
