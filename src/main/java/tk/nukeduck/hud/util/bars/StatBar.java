package tk.nukeduck.hud.util.bars;

import java.util.List;

import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Direction.Options;
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

	protected abstract List<Bounds> getIcons(int pointsIndex);

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

	public Bounds render(Point anchor, Direction alignment, Direction contentAlignment) {
		Bounds bounds = new Bounds(getSize()).alignedAround(anchor, alignment);
		render(bounds, contentAlignment);
		return bounds;
	}

	public void render(Bounds bounds, Direction contentAlignment) {
		if(!Options.CORNERS.isValid(contentAlignment)) {
			throw new IllegalArgumentException("Bar must start in a corner");
		}

		GlUtil.enableBlendTranslucent();
		GlUtil.color(Colors.WHITE);

		Direction rowWise       = contentAlignment.withColumn(1).mirrorRow();
		Direction columnWise    = contentAlignment.withRow(1).mirrorColumn();
		Direction iconAlignment = contentAlignment.withRow(1);

		Bounds icon = new Bounds(getIconSize(), getIconSize()).anchoredTo(bounds, contentAlignment);
		Bounds rowReturn = new Bounds(icon);

		final int max = getMaximum(), rowPoints = getRowPoints();

		for(int i = 0; i < max; rowReturn = rowReturn.anchoredTo(rowReturn, rowWise, true), icon = rowReturn) {
			for(int x = 0; x < rowPoints && i < max; i += 2, x += 2, icon = icon.anchoredTo(icon.withInset(1), columnWise, true)) {
				Bounds bounced = icon.addPosition(0, getIconBounce(i));

				for(Bounds texture : getIcons(i)) {
					if(texture != null) {
						texture = ensureNative(texture, iconAlignment);
						GlUtil.drawTexturedModalRect(bounced, texture);
					}
				}
			}
		}
	}

	public Bounds ensureNative(Bounds texture, Direction alignment) {
		Direction nativeAlignment = getNativeAlignment();

		if(nativeAlignment != null && nativeAlignment != alignment) {
			return texture.flippedHorizontal();
		} else {
			return texture;
		}
	}

	public Point getSize() {
		int rowPoints = getRowPoints();
		int rows = MathUtil.ceilDiv(getMaximum(), rowPoints);

		return new Point(
			8 * MathUtil.ceilDiv(rowPoints, 2) + 1,
			getRowSpacing() * (rows - 1) + getIconSize());
	}
}
