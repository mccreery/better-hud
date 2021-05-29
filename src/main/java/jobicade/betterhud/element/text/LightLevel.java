package jobicade.betterhud.element.text;

import jobicade.betterhud.geom.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

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
            light = Minecraft.getInstance().level.getChunkSource().getLightEngine().getRawBrightness(position, 0);
        }
        return Arrays.asList(getLocalizedName() + ": " + light);
    }
}
