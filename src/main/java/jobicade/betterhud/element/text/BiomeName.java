package jobicade.betterhud.element.text;

import static jobicade.betterhud.BetterHud.MC;

import java.util.Arrays;
import java.util.List;

import net.minecraft.util.math.BlockPos;

public class BiomeName extends TextElement {
    public BiomeName() {
        super("biome");
    }

    @Override
    protected List<String> getText() {
        BlockPos pos = new BlockPos((int)MC.player.posX, 0, (int)MC.player.posZ);

        return Arrays.asList(getLocalizedName() + ": " + MC.world.getBiomeForCoordsBody(pos).getBiomeName());
    }
}
