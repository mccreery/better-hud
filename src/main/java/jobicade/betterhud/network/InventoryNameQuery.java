package jobicade.betterhud.network;

import java.util.function.Supplier;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.registry.OverlayElements;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class InventoryNameQuery {
    public static class Request {
        public final BlockPos blockPos;

        public Request(BlockPos blockPos) {
            this.blockPos = blockPos;
        }

        public Request(PacketBuffer packetBuffer) {
            this(packetBuffer.readBlockPos());
        }

        public void encode(PacketBuffer packetBuffer) {
            packetBuffer.writeBlockPos(blockPos);
        }

        public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
            //System.out.println("Received block request " + message.getBlockPos());
            NetworkEvent.Context context = contextSupplier.get();

            TileEntity tileEntity = context.getSender().getEntityWorld().getTileEntity(blockPos);

            if (tileEntity instanceof INameable) {
                ITextComponent inventoryName = ((INameable)tileEntity).getDisplayName();
                BetterHud.NET_WRAPPER.send(PacketDistributor.PLAYER.with(context::getSender), new Response(blockPos, inventoryName));
            }
        }
    }

    public static class Response extends InventoryNameQuery {
        public final BlockPos blockPos;
        public final ITextComponent inventoryName;

        public Response(BlockPos blockPos, ITextComponent inventoryName) {
            this.blockPos = blockPos;
            this.inventoryName = inventoryName;
        }

        public Response(PacketBuffer packetBuffer) {
            this(packetBuffer.readBlockPos(), packetBuffer.readTextComponent());
        }

        public void encode(PacketBuffer packetBuffer) {
            packetBuffer.writeBlockPos(blockPos);
            packetBuffer.writeTextComponent(inventoryName);
        }

        public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
            //System.out.println("Received block response " + message.getBlockPos() + " for name " + message.getInventoryName());

            OverlayElements.BLOCK_VIEWER.onNameReceived(blockPos, inventoryName);
        }
    }
}
