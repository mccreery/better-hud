package jobicade.betterhud.network;

import java.util.function.Supplier;

import jobicade.betterhud.element.HudElement;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class InventoryNameRes {
    private final BlockPos pos;
    private final ITextComponent name;

    public InventoryNameRes(BlockPos pos, ITextComponent name) {
        this.pos = pos;
        this.name = name;
    }

    public void consume(Supplier<Context> contextSupplier) {
        HudElement.BLOCK_VIEWER.onNameReceived(pos, name);
    }

    public BlockPos getPos() {
        return pos;
    }

    public ITextComponent getName() {
        return name;
    }

    public void encode(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeTextComponent(name);
    }

    public static InventoryNameRes decode(PacketBuffer buf) {
        return new InventoryNameRes(buf.readBlockPos(), buf.readTextComponent());
    }
}
