package jobicade.betterhud.network;

import jobicade.betterhud.BetterHud;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class InventoryNameRequest {
    public final BlockPos blockPos;

    public InventoryNameRequest(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public InventoryNameRequest(PacketBuffer packetBuffer) {
        this(packetBuffer.readBlockPos());
    }

    public void encode(PacketBuffer packetBuffer) {
        packetBuffer.writeBlockPos(blockPos);
    }

    public void handle(Supplier<Context> contextSupplier) {
        //System.out.println("Received block request " + message.getBlockPos());
        Context context = contextSupplier.get();

        TileEntity tileEntity = context.getSender().getCommandSenderWorld().getBlockEntity(blockPos);

        if (tileEntity instanceof INameable) {
            ITextComponent inventoryName = ((INameable)tileEntity).getDisplayName();
            BetterHud.NET_WRAPPER.send(PacketDistributor.PLAYER.with(context::getSender), new InventoryNameResponse(blockPos, inventoryName));
        }
        context.setPacketHandled(true);
    }
}
