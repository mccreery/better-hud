package jobicade.betterhud.element.text;

import static jobicade.betterhud.BetterHud.MC;

import java.util.Arrays;
import java.util.List;

import net.minecraft.util.math.BlockPos;

public class LightLevel extends TextElement {
    public LightLevel() {
        super("lightLevel");
    }

    @Override
    protected List<String> getText() {
        BlockPos position = new BlockPos(MC.player.getPosX(), MC.player.getPosY(), MC.player.getPosZ());
        int light = MC.world.getChunkProvider().getLightManager().getLightSubtracted(position, 0);

        /*int light = 0;
        if(MC.world != null && MC.world.isBlockLoaded(position)) {
            light = MC.world.getLightFor(EnumSkyBlock.SKY, position) - MC.world.getSkylightSubtracted();
            light = Math.max(light, MC.world.getLightFor(EnumSkyBlock.BLOCK, position));
        }*/
        return Arrays.asList(getLocalizedName() + ": " + light);
    }
}
