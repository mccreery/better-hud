package tk.nukeduck.hud.util.bars;

import net.minecraft.entity.player.EntityPlayer;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;

public class StatBarFood extends StatBarBasic {
	private final EntityPlayer entity;

	public StatBarFood(EntityPlayer entity) {
		this.entity = entity;
	}

	@Override
	protected int getCurrent() {
		return entity.getFoodStats().getFoodLevel();
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
}
