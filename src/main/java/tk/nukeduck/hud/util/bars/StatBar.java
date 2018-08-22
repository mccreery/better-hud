package tk.nukeduck.hud.util.bars;

import java.util.List;

import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Direction.Options;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.MathUtil;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.Renderable;

public abstract class StatBar<T> extends Renderable {
	protected int getMaximum() {
		return 20;
	}

	protected T host;

	public void setHost(T host) {
		this.host = host;
	}

	protected boolean shouldCompress() {
		return false;
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

	public Bounds render(Point anchor, Direction alignment, Direction contentAlignment) {
		Bounds bounds = new Bounds(getSize()).align(anchor, alignment);
		render(bounds, contentAlignment);
		return bounds;
	}

	@Override
	public void renderUnsafe(Bounds bounds, Direction contentAlignment) {
		if(!Options.CORNERS.isValid(contentAlignment)) {
			throw new IllegalArgumentException("Bar must start in a corner");
		}

		GlUtil.enableBlendTranslucent();
		GlUtil.color(Colors.WHITE);

		Direction columnWise = contentAlignment.withRow(1).mirrorColumn();
		Bounds icon = new Bounds(getIconSize(), getIconSize()).anchor(bounds, contentAlignment);
		Bounds rowReturn = new Bounds(icon);

		final int max = getMaximum(), rowPoints = getRowPoints();
		int rowSpacing = getRowSpacing();
		if(contentAlignment.getRow() == 2) rowSpacing = -rowSpacing;
		int i = 0;

		Point textPosition = null;
		String text = null;

		if(shouldCompress()) {
			int rows = (max - 1) / rowPoints;
			i = rows * rowPoints;

			for(int x = 0; x < rowPoints; x += 2, icon = icon.align(icon.getAnchor(Direction.CENTER), columnWise.mirrorColumn())) {
				drawIcon(x, icon, contentAlignment);
			}
			textPosition = icon.getAnchor(columnWise);
			text = "x" + rows;

			rowReturn = rowReturn.translate(0, rowSpacing);
			icon = rowReturn;
		}

		for(; i < max; rowReturn = rowReturn.translate(0, rowSpacing), icon = rowReturn) {
			for(int x = 0; x < rowPoints && i < max; i += 2, x += 2, icon = icon.anchor(icon.grow(-1), columnWise, true)) {
				drawIcon(i, icon, contentAlignment);
			}
		}

		if(text != null) {
			GlUtil.drawString(text, textPosition, columnWise.mirrorColumn(), Colors.WHITE);
		}
	}

	protected void drawIcon(int i, Bounds bounds, Direction contentAlignment) {
		bounds = bounds.translate(0, getIconBounce(i));

		for(Bounds texture : getIcons(i)) {
			if(texture != null) {
				texture = ensureNative(texture, contentAlignment.withRow(1));
				GlUtil.drawTexturedModalRect(bounds, texture);
			}
		}
	}

	protected Bounds ensureNative(Bounds texture, Direction alignment) {
		Direction nativeAlignment = getNativeAlignment();

		if(nativeAlignment != null && nativeAlignment != alignment) {
			return texture.scale(-1, 1).withX(texture.getRight());
		} else {
			return texture;
		}
	}

	@Override
	public Point getSize() {
		int rowPoints = getRowPoints();
		Point rowSize = new Point((getIconSize() - 1) * MathUtil.ceilDiv(rowPoints, 2) + 1, getIconSize());

		int rows;
		if(shouldCompress()) {
			rows = 2;
		} else {
			rows = MathUtil.ceilDiv(getMaximum(), rowPoints);
		}

		return rowSize.add(0, (rows - 1) * getRowSpacing());
	}
}
