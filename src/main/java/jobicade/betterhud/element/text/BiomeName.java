package jobicade.betterhud.element.text;

import java.util.Arrays;
import java.util.List;

import jobicade.betterhud.geom.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

public class BiomeName extends TextElement {
	@Override
	public void loadDefaults() {
		super.loadDefaults();
		position.setPreset(Direction.NORTH);
		settings.priority.set(-1);
	}

	public BiomeName() {
		super("biome");
	}

	@Override
	protected List<String> getText() {
		BlockPos pos = new BlockPos((int)Minecraft.getMinecraft().player.posX, 0, (int)Minecraft.getMinecraft().player.posZ);

		return Arrays.asList(getLocalizedName() + ": " + Minecraft.getMinecraft().world.getBiomeForCoordsBody(pos).getBiomeName());
	}
}
