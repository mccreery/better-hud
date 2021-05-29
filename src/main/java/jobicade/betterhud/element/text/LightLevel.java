package jobicade.betterhud.element.text;

import jobicade.betterhud.geom.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;

import java.util.Arrays;
import java.util.List;

public class LightLevel extends TextElement {
    @Override
    public void loadDefaults() {
        super.loadDefaults();
        position.setPreset(Direction.SOUTH_EAST);
    }

    public LightLevel() {
        super("lightLevel");
    }

    @Override
    protected List<String> getText() {
        BlockPos position = new BlockPos(Minecraft.getInstance().player.getX(), Minecraft.getInstance().player.getY(), Minecraft.getInstance().player.getZ());

        int light = 0;
        if(Minecraft.getInstance().level != null && Minecraft.getInstance().level.hasChunkAt(position)) {
            light = Minecraft.getInstance().level.func_175642_b(EnumSkyBlock.SKY, position) - Minecraft.getInstance().level.func_72967_a(1.0F);
            light = Math.max(light, Minecraft.getInstance().level.func_175642_b(EnumSkyBlock.BLOCK, position));
        }
        return Arrays.asList(getLocalizedName() + ": " + light);
    }
}
