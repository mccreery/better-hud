package tk.nukeduck.hud.util;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.Arrays;
import java.util.Collection;

public class StringGroup {
	private final Collection<String> source;

	private Direction alignment;
	private int color;
	private int spacing;
	private int gutter;
	private boolean vertical;

	public StringGroup(String... source) {
		this(Arrays.asList(source));
	}

	public StringGroup(Collection<String> source) {
		this.source = source;

		color = Colors.WHITE;
		alignment = Direction.NORTH_WEST;
		spacing = 0;
		gutter = 2;
		vertical = true;
	}

	public StringGroup(StringGroup group) {
		this(group, group.source);
	}

	public StringGroup(StringGroup group, Collection<String> source) {
		this.source = source;

		this.color = group.color;
		this.alignment = group.alignment;
		this.spacing = group.spacing;
		this.gutter = group.gutter;
		this.vertical = group.vertical;
	}

	public StringGroup setAlignment(Direction alignment) {
		this.alignment = alignment;
		return this;
	}
	public StringGroup setColor(int color) {
		this.color = color;
		return this;
	}
	public StringGroup setSpacing(int spacing) {
		this.spacing = spacing;
		return this;
	}
	public StringGroup setGutter(int gutter) {
		this.gutter = gutter;
		return this;
	}

	public StringGroup setRow() {return setOrientation(false);}
	public StringGroup setColumn() {return setOrientation(true);}

	public StringGroup setOrientation(boolean vertical) {
		this.vertical = vertical;
		return this;
	}

	/** @return The size of a row or column
	 * @see #drawLines(Collection, Point, Direction, int, int, int, boolean) */
	public Point getSize() {
		Point cellSize = getCellSize();

		if(vertical) {
			return cellSize.withY((cellSize.getY() + gutter) * source.size() - gutter);
		} else {
			return cellSize.withX((cellSize.getX() + gutter) * source.size() - gutter);
		}
	}

	/** @return The size of a single cell, not including the trailing gutter */
	public Point getCellSize() {
		int maxWidth = 0;

		for(String string : source) {
			int lineWidth = MC.fontRenderer.getStringWidth(string);
			if(lineWidth > maxWidth) maxWidth = lineWidth;
		}

		if(vertical) {
			return new Point(maxWidth, Math.max(spacing - gutter, MC.fontRenderer.FONT_HEIGHT));
		} else {
			return new Point(Math.max(maxWidth, spacing - gutter), MC.fontRenderer.FONT_HEIGHT);
		}
	}

	public Bounds draw(Bounds container) {
		return draw(container.getAnchor(alignment));
	}

	/** Draws lines of text in a row or column
	 * If lines overlap or are too close, {@code spacing} will be increased
	 *
	 * @param origin The origin point of the column
	 * @param alignment The alignment of the column and the lines inside it
	 * @param spacing The spacing between the same anchor of adjacent lines
	 * @param gutter The minimum space between the end of a line and the start of the next
	 * @see #getColumnSize(Collection, int, int)
	 * @see #drawRow(Collection, Point, Direction, int, int, int) */
	public Bounds draw(Point origin) {
		Bounds lineBounds = new Bounds(getCellSize());

		Bounds bounds;
		if(vertical) {
			bounds = lineBounds.withHeight((lineBounds.getHeight() + gutter) * source.size() - gutter);
		} else {
			bounds = lineBounds.withWidth((lineBounds.getWidth() + gutter) * source.size() - gutter);
		}
		bounds = bounds.align(origin, alignment);

		lineBounds = lineBounds.withPosition(bounds.getPosition());

		for(String line : source) {
			GlUtil.drawString(line, lineBounds.getAnchor(alignment), alignment, color);

			if(vertical) {
				lineBounds = lineBounds.withY(lineBounds.getBottom() + gutter);
			} else {
				lineBounds = lineBounds.withX(lineBounds.getRight() + gutter);
			}
		}
		return bounds;
	}
}
