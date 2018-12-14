package jobicade.betterhud.element.text;

import static jobicade.betterhud.BetterHud.MC;

import java.util.Arrays;
import java.util.List;

import net.minecraft.util.math.BlockPos;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.util.Direction;
import jobicade.betterhud.util.Direction.Options;

public class BiomeName extends TextElement {
	@Override
	public void loadDefaults() {
		super.loadDefaults();
		position.setPreset(Direction.NORTH);
		settings.priority.set(-1);
	}

	public BiomeName() {
		super("biome", new SettingPosition(Options.TOP_BOTTOM, Options.NONE));
	}

	@Override
	protected List<String> getText() {
		BlockPos pos = new BlockPos((int)MC.player.posX, 0, (int)MC.player.posZ);

		return Arrays.asList(getLocalizedName() + ": " + MC.world.getBiomeForCoordsBody(pos).getBiomeName());
	}
}
