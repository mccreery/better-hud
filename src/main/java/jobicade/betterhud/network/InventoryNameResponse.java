package jobicade.betterhud.network;

import jobicade.betterhud.element.HudElement;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class InventoryNameResponse {
    public final BlockPos blockPos;
    public final ITextComponent inventoryName;

    public InventoryNameResponse(BlockPos blockPos, ITextComponent inventoryName) {
        this.blockPos = blockPos;
        this.inventoryName = inventoryName;
    }

    public InventoryNameResponse(PacketBuffer packetBuffer) {
        this(packetBuffer.readBlockPos(), packetBuffer.readComponent());
    }

    public void encode(PacketBuffer packetBuffer) {
        packetBuffer.writeBlockPos(blockPos);
        packetBuffer.writeComponent(inventoryName);
    }

    public void handle(Supplier<Context> contextSupplier) {
        //System.out.println("Received block response " + message.getBlockPos() + " for name " + message.getInventoryName());

        HudElement.BLOCK_VIEWER.onNameReceived(blockPos, inventoryName);
        contextSupplier.get().setPacketHandled(true);
    }
}
