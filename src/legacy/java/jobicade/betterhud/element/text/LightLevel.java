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
        BlockPos position = new BlockPos(Minecraft.getMinecraft().player.posX, Minecraft.getMinecraft().player.posY, Minecraft.getMinecraft().player.posZ);

        int light = 0;
        if(Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().world.isBlockLoaded(position)) {
            light = Minecraft.getMinecraft().world.getLightFor(EnumSkyBlock.SKY, position) - Minecraft.getMinecraft().world.calculateSkylightSubtracted(1.0F);
            light = Math.max(light, Minecraft.getMinecraft().world.getLightFor(EnumSkyBlock.BLOCK, position));
        }
        return Arrays.asList(getLocalizedName() + ": " + light);
    }
}
