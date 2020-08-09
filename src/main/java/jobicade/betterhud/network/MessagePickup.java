package jobicade.betterhud.network;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class MessagePickup {
    private ItemStack stack;

    public MessagePickup() {}
    public MessagePickup(ItemStack stack) {
        this.stack = stack;
    }

    public MessagePickup(PacketBuffer packetBuffer) {
        this(packetBuffer.readItemStack());
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public void encode(PacketBuffer packetBuffer) {
        packetBuffer.writeItemStack(stack);
    }
}
