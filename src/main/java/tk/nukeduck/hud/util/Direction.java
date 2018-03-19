package tk.nukeduck.hud.util;

import net.minecraft.client.resources.I18n;

/** One of 8 cardinal directions or {@link CENTER}, the null direction */
public enum Direction {
	NORTH_WEST("northWest"),
	NORTH("north"),
	NORTH_EAST("northEast"),

	WEST ("west"),
	CENTER("center"),
	EAST ("east"),

	SOUTH_WEST("southWest"),
	SOUTH("south"),
	SOUTH_EAST("southEast");

	public static final int CORNERS    = getFlags(NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST);
	public static final int SIDES      = getFlags(NORTH, EAST, SOUTH, WEST);
	public static final int TOP        = getFlags(NORTH_WEST, NORTH, NORTH_EAST);
	public static final int HORIZONTAL = getFlags(WEST, CENTER, EAST);
	public static final int BOTTOM     = getFlags(SOUTH_WEST, SOUTH, SOUTH_EAST);
	public static final int LEFT       = getFlags(NORTH_WEST, WEST, SOUTH_WEST);
	public static final int VERTICAL   = getFlags(NORTH, CENTER, SOUTH);
	public static final int RIGHT      = getFlags(NORTH_EAST, EAST, SOUTH_EAST);
	public static final int ALL        = TOP | HORIZONTAL | BOTTOM;

	private static final int[] ROWS = new int[] {TOP, HORIZONTAL, BOTTOM};
	private static final int[] COLUMNS = new int[] {LEFT, VERTICAL, RIGHT};

	public final String name;

	Direction(String name) {
		this.name = name;
	}

	public String getUnlocalizedName() {
		return "betterHud.value." + name;
	}

	public String getLocalizedName() {
		return I18n.format(getUnlocalizedName());
	}

	public int getColumn() {return ordinal() % 3;}
	public int getRow() {return ordinal() / 3;}

	public Direction toColumn(int column) {return get(getRow(), column);}
	public Direction toRow(int row) {return get(row, getColumn());}

	public Direction mirrorRow() {return get(2 - getRow(), getColumn());}
	public Direction mirrorColumn() {return get(getRow(), 2 - getColumn());}
	public Direction mirror() {return get(2 - getRow(), 2 - getColumn());}

	public boolean in(int flags) {
		return (flags & flag()) != 0;
	}

	public int flag() {return 1 << ordinal();}

	public static int getFlags(Direction... directions) {
		int flags = 0;

		for(Direction direction : directions) {
			flags |= direction.flag();
		}
		return flags;
	}

	public static int getRowFlags(int y) {
		return ROWS[y];
	}
	public static int getColumnFlags(int x) {
		return COLUMNS[x];
	}

	/** @return The anchor point between zero and {@code size}
	 * corresponding to this direction */
	public Point getAnchor(Point size) {
		return size.scale(getColumn() / 2f, getRow() / 2f);
	}

	/** @return The anchor point within {@code bounds}
	 * corresponding to this direction */
	public Point getAnchor(Bounds bounds) {
		return bounds.position.add(getAnchor(bounds.size));
	}

	/** {@code point} defaults to the north-west corner of {@code bounds}
	 * @see #align(Bounds, Point) */
	public <T extends Bounds> T align(T bounds) {
		return align(bounds, bounds.position);
	}

	/** Aligns the side(s) of {@code bounds} around {@code point},
	 * for example {@link EAST} aligns the east edge of {@code bounds} to {@code point} */
	public <T extends Bounds> T align(T bounds, Point point) {
		bounds.position = point.sub(getAnchor(bounds.size));
		return bounds;
	}

	/** Aligns the side(s) of {@code bounds} to the side(s) of {@code container} */
	public <T extends Bounds> T anchor(T bounds, Bounds container) {
		return align(bounds, getAnchor(container));
	}

	public static Direction get(int row, int column) {
		return get(row * 3 + column);
	}

	public static Direction get(int i) {
		return values()[i];
	}

	public static Direction get(String name) {
		for(Direction direction : values()) {
			if(direction.name.equals(name)) return direction;
		}
		return Direction.NORTH_WEST;
	}
}
