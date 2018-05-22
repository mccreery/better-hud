package tk.nukeduck.hud.util.bars;

import net.minecraft.entity.player.EntityPlayer;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;

public class StatBarFood extends StatBar {
	private final EntityPlayer entity;

	public StatBarFood(EntityPlayer entity) {
		this.entity = entity;
	}

	@Override
	protected int getCurrent() {
		return entity.getFoodStats().getFoodLevel();
	}

	@Override
	protected Bounds getIcon(IconType icon, Direction alignment, int pointsIndex) {
		Bounds bounds;

		switch(icon) {
			case BACKGROUND: bounds = new Bounds(16, 27, 9, 9); break;
			case HALF:       bounds = new Bounds(61, 27, 9, 9); break;
			case FULL:       bounds = new Bounds(52, 27, 9, 9); break;
			default:         return null;
		}

		if(!alignment.in(Direction.RIGHT)) { // align left
			bounds.x(bounds.right());
			bounds.width(-bounds.width());
		}
		return bounds;
	}
}
