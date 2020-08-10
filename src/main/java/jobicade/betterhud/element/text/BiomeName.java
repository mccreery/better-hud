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
        BlockPos pos = new BlockPos((int)MC.player.getPosX(), 0, (int)MC.player.getPosZ());

        return Arrays.asList(getLocalizedName() + ": " + MC.world.getBiome(pos).getDisplayName());
    }
}
