package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.util.math.BlockPos;
import tk.nukeduck.hud.util.Direction;

public class BiomeName extends TextElement {
	@Override
	public void loadDefaults() {
		super.loadDefaults();
		position.set(Direction.NORTH);
	}

	public BiomeName() {
		super("biome", Direction.CORNERS | Direction.NORTH.flag());
	}

	@Override
	protected String[] getText() {
		BlockPos pos = new BlockPos((int)MC.player.posX, 0, (int)MC.player.posZ);
		return new String[] {getLocalizedName() + ": " + MC.world.getBiomeForCoordsBody(pos).getBiomeName()};
	}
}
