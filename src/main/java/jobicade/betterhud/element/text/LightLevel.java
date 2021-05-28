package jobicade.betterhud.element.text;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import jobicade.betterhud.geom.Direction;

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
        BlockPos position = new BlockPos(Minecraft.getInstance().player.field_70165_t, Minecraft.getInstance().player.field_70163_u, Minecraft.getInstance().player.field_70161_v);

        int light = 0;
        if(Minecraft.getInstance().level != null && Minecraft.getInstance().level.hasChunkAt(position)) {
            light = Minecraft.getInstance().level.func_175642_b(EnumSkyBlock.SKY, position) - Minecraft.getInstance().level.func_72967_a(1.0F);
            light = Math.max(light, Minecraft.getInstance().level.func_175642_b(EnumSkyBlock.BLOCK, position));
        }
        return Arrays.asList(getLocalizedName() + ": " + light);
    }
}
