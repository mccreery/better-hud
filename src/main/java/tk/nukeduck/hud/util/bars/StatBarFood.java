package tk.nukeduck.hud.util.bars;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.MathUtil;

public class StatBarFood extends StatBarBasic<EntityPlayer> {
	private final Random random = new Random();

	@Override
	protected int getCurrent() {
		return host.getFoodStats().getFoodLevel();
	}

	@Override
	protected Bounds getIcon(IconType icon, int pointsIndex) {
		switch(icon) {
			case BACKGROUND: return new Bounds(16, 27, 9, 9);
			case HALF:       return new Bounds(61, 27, 9, 9);
			case FULL:       return new Bounds(52, 27, 9, 9);
			default:         return null;
		}
	}

	@Override
	public Direction getNativeAlignment() {
		return Direction.EAST;
	}

	@Override
	protected int getIconBounce(int pointsIndex) {
		if(host.getFoodStats().getSaturationLevel() <= 0 && MC.ingameGUI.getUpdateCounter() % (getCurrent() * 3 + 1) == 0) {
			return MathUtil.randomRange(-1, 2, random);
		} else {
			return 0;
		}
	}

	@Override
	public void render(Bounds bounds, Direction contentAlignment) {
		random.setSeed(MC.ingameGUI.getUpdateCounter());
		super.render(bounds, contentAlignment);
	}
}
