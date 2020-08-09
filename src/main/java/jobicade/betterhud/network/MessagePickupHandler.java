package jobicade.betterhud.network;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import jobicade.betterhud.registry.OverlayElements;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class MessagePickupHandler implements BiConsumer<MessagePickup, Supplier<NetworkEvent.Context>> {
    @Override
    public void accept(MessagePickup message, Supplier<Context> contextSupplier) {
        OverlayElements.PICKUP.refreshStack(message.getStack());
    }
}
