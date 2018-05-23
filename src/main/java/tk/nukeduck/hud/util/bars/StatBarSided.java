package tk.nukeduck.hud.util.bars;

import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;

public abstract class StatBarSided extends StatBar {
	protected abstract Bounds getIcon(IconType icon, int pointsIndex);

	/** @return Either {@link Direction#WEST} or {@link Direction#EAST}
	 * to indicate which alignment the icons do not need to be flipped for */
	public abstract Direction getIconAlignment();

	@Override
	protected Bounds getIcon(IconType icon, Direction alignment, int pointsIndex) {
		Bounds texture = getIcon(icon, pointsIndex);

		if(alignment.in(Direction.RIGHT) != getIconAlignment().in(Direction.RIGHT)) {
			texture.x(texture.right());
			texture.width(-texture.width());
		}
		return texture;
	}
}
