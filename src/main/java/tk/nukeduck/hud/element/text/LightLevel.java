package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.Arrays;
import java.util.List;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import tk.nukeduck.hud.util.Direction;

public class LightLevel extends TextElement {
	@Override
	public void loadDefaults() {
		super.loadDefaults();
		position.set(Direction.NORTH_EAST);
	}

	public LightLevel() {
		super("lightLevel");
	}

	@Override
	protected List<String> getText() {
		BlockPos position = new BlockPos(MC.player.posX, MC.player.posY, MC.player.posZ);

		int light = 0;
		if(MC.world != null && MC.world.isBlockLoaded(position)) {
			light = MC.world.getLightFor(EnumSkyBlock.SKY, position) - MC.world.calculateSkylightSubtracted(1.0F);
			light = Math.max(light, MC.world.getLightFor(EnumSkyBlock.BLOCK, position));
		}
		return Arrays.asList(getLocalizedName() + ": " + light);
	}

	@Override
	public boolean shouldRender() {
		return MC.player != null && MC.world != null;
	}
}
