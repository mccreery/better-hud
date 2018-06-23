package tk.nukeduck.hud.util.bars;

import java.util.List;

import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.MathUtil;
import tk.nukeduck.hud.util.Point;

public abstract class StatBar<T> {
	protected int getMaximum() {
		return 20;
	}

	protected T host;

	public void setHost(T host) {
		this.host = host;
	}

	protected abstract List<Bounds> getIcons(Direction alignment, int pointsIndex);

	protected int getIconBounce(int pointsIndex) {
		return 0;
	}

	protected int getRowSpacing() {
		return getIconSize();
	}

	protected int getIconSize() {
		return 9;
	}

	protected int getRowPoints() {
		return 20;
	}

	/** @return Either {@link Direction#WEST}, {@link Direction#EAST} or {@code null}
	 * depending on the natural direction of the icons on the spritesheet.
	 *
	 * <p>Icon sprites will be flipped horizontally if the native alignment
	 * does not agree with the current alignment */
	protected Direction getNativeAlignment() {
		return null;
	}

	public boolean shouldRender() {
		return true;
	}

	public void render(Point position, Direction alignment) {
		GlUtil.enableBlendTranslucent();
		GlUtil.color(Colors.WHITE);

		final int max = getMaximum(), rowPoints = getRowPoints();
		int resetX = position.getX();

		int iconSpacing = 8;
		if(alignment == Direction.EAST) {
			resetX += iconSpacing * (MathUtil.ceilDiv(rowPoints, 2) - 1);
			iconSpacing = -iconSpacing;
		}

		int x = resetX;
		int y = position.getY();

		for(int i = 0; i < max; x = resetX, y += getRowSpacing()) {
			for(int j = 0; j < rowPoints && i < max; i += 2, j += 2, x += iconSpacing) {
				int iconBounce = getIconBounce(i);

				for(Bounds texture : getIcons(alignment, i)) {
					if(texture != null) {
						texture = ensureNative(texture, alignment);

						GlUtil.drawTexturedModalRect(x, y + iconBounce,
							texture.getX(), texture.getY(), texture.getWidth(), texture.getHeight());
					}
				}
			}
		}
	}

	public Bounds ensureNative(Bounds texture, Direction alignment) {
		return alignment == getNativeAlignment() ? texture : texture.flippedHorizontal();
	}

	public Point getSize() {
		int rowPoints = getRowPoints();
		int rows = MathUtil.ceilDiv(getMaximum(), rowPoints);

		return new Point(
			8 * MathUtil.ceilDiv(rowPoints, 2) + 1,
			getRowSpacing() * (rows - 1) + getIconSize());
	}
}
