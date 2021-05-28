package jobicade.betterhud.network;

import jobicade.betterhud.element.HudElement;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class MessagePickup {
    private ItemStack stack;

    public MessagePickup() {}
    public MessagePickup(ItemStack stack) {
        this.stack = stack;
    }

    public MessagePickup(PacketBuffer packetBuffer) {
        this(packetBuffer.readItem());
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public void encode(PacketBuffer packetBuffer) {
        packetBuffer.writeItem(stack);
    }

    public static void handle(MessagePickup message, Supplier<Context> context) {
        HudElement.PICKUP.refreshStack(message.getStack());
        context.get().setPacketHandled(true);
    }
}
