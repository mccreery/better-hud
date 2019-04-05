package jobicade.betterhud.network;

import static jobicade.betterhud.BetterHud.NET_WRAPPER;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class InventoryNameReq {
    private final BlockPos pos;

    public InventoryNameReq(BlockPos pos) {
        this.pos = pos;
    }

    public void consume(Supplier<Context> contextSupplier) {
        Context context = contextSupplier.get();
        TileEntity tileEntity = context.getSender().world.getTileEntity(pos);

        if(tileEntity instanceof INameable) {
            InventoryNameRes response = new InventoryNameRes(pos, ((INameable)tileEntity).getDisplayName());
            NET_WRAPPER.reply(response, context);
        }
    }

    public BlockPos getPos() {
        return pos;
    }

    public void encode(PacketBuffer buf) {
        buf.writeBlockPos(pos);
    }

    public static InventoryNameReq decode(PacketBuffer buf) {
        return new InventoryNameReq(buf.readBlockPos());
    }
}
