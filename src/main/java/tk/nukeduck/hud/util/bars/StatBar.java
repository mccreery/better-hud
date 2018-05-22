package tk.nukeduck.hud.util.bars;

import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;

public abstract class StatBar {
	protected abstract int getCurrent();
	protected abstract int getMaximum();

	protected enum IconType {
		BACKGROUND, HALF, FULL;
	}

	protected abstract Bounds getIcon(IconType icon, Direction alignment, int pointsIndex);
	protected abstract int getIconBounce(int pointsIndex);

	protected abstract int getRowSpacing();

	protected int getIconSize() {
		return 9;
	}

	protected int getRowPoints() {
		return 20;
	}

	public void render(Point position, Direction alignment) {
		GlUtil.enableBlendTranslucent();
		GlUtil.color(Colors.WHITE);
		Point icon = new Point(position);

		final int current = getCurrent(), max = getMaximum();

		for(int i = 0; i < max; icon.x = position.x, icon.y += 9) {
			for(int j = 0; j < 20 && i < max; i += 2, j += 2, icon.x += 8) {
				Bounds background = getIcon(IconType.BACKGROUND, alignment, i);

				if(background != null) {
					GlUtil.drawTexturedModalRect(icon, background);
				}

				if(i < current) {
					Bounds foreground = getIcon(i + 1 < current ? IconType.FULL : IconType.HALF, alignment, i);

					GlUtil.drawTexturedModalRect(icon.x, icon.y + getIconBounce(i),
						foreground.x(), foreground.y(), foreground.width(), foreground.height());
				}
			}
		}
	}

	public Point getSize() {
		int rowPoints = getRowPoints();
		int rows = BetterHud.ceilDiv(getMaximum(), rowPoints);

		return new Point(
			8 * BetterHud.ceilDiv(rowPoints, 2) + 1,
			getRowSpacing() * (rows - 1) + getIconSize());
	}
}
