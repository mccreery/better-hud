package jobicade.betterhud.util.bars;

import java.util.List;

import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.MathUtil;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.util.Renderable;

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

	protected abstract List<Rect> getIcons(int pointsIndex);

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

	public Rect render(Point anchor, Direction alignment, Direction contentAlignment) {
		Rect bounds = new Rect(getSize()).align(anchor, alignment);
		render(bounds, contentAlignment);
		return bounds;
	}

	@Override
	public void renderUnsafe(Rect bounds, Direction contentAlignment) {
		if(!DirectionOptions.CORNERS.isValid(contentAlignment)) {
			throw new IllegalArgumentException("Bar must start in a corner");
		}

		Direction columnWise = contentAlignment.withRow(1).mirrorCol();
		Rect icon = new Rect(getIconSize(), getIconSize()).anchor(bounds, contentAlignment);
		Rect rowReturn = new Rect(icon);

		final int max = getMaximum(), rowPoints = getRowPoints();
		int rowSpacing = getRowSpacing();
		if(contentAlignment.getRow() == 2) rowSpacing = -rowSpacing;
		int i = 0;

		Point textPosition = null;
		String text = null;

		if(shouldCompress()) {
			int rows = (max - 1) / rowPoints;
			i = rows * rowPoints;

			for(int x = 0; x < rowPoints; x += 2, icon = icon.align(icon.getAnchor(Direction.CENTER), columnWise.mirrorCol())) {
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
			GlUtil.drawString(text, textPosition, columnWise.mirrorCol(), Color.WHITE);
		}
	}

	protected void drawIcon(int i, Rect bounds, Direction contentAlignment) {
		bounds = bounds.translate(0, getIconBounce(i));

		for(Rect texture : getIcons(i)) {
			if(texture != null) {
				texture = ensureNative(texture, contentAlignment.withRow(1));
				GlUtil.drawTexturedModalRect(bounds, texture);
			}
		}
	}

	protected Rect ensureNative(Rect texture, Direction alignment) {
		Direction nativeAlignment = getNativeAlignment();

		if(nativeAlignment != null && nativeAlignment != alignment) {
			return texture.scale(-1, 1).withX(texture.getRight());
		} else {
			return texture;
		}
	}

	@Override
	public Size getSize() {
		int rowPoints = getRowPoints();
		Size rowSize = new Size((getIconSize() - 1) * MathUtil.ceilDiv(rowPoints, 2) + 1, getIconSize());

		int rows;
		if(shouldCompress()) {
			rows = 2;
		} else {
			rows = MathUtil.ceilDiv(getMaximum(), rowPoints);
		}

		return rowSize.add(0, (rows - 1) * getRowSpacing());
	}
}
