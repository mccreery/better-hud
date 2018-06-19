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

	public static final int CORNERS    = NORTH_WEST.getFlag() | NORTH_EAST.getFlag() | SOUTH_WEST.getFlag() | SOUTH_EAST.getFlag();
	public static final int SIDES      = NORTH.getFlag() | EAST.getFlag() | SOUTH.getFlag() | WEST.getFlag();

	public static final int TOP = NORTH_WEST.getFlag() | NORTH.getFlag() | NORTH_EAST.getFlag();
	public static final int HORIZONTAL = WEST.getFlag() | CENTER.getFlag() | EAST.getFlag();
	public static final int BOTTOM = SOUTH_WEST.getFlag() | SOUTH.getFlag() | SOUTH_EAST.getFlag();

	public static final int LEFT = NORTH_WEST.getFlag() | WEST.getFlag() | SOUTH_WEST.getFlag();
	public static final int VERTICAL = NORTH.getFlag() | CENTER.getFlag() | SOUTH.getFlag();
	public static final int RIGHT = NORTH_EAST.getFlag() | EAST.getFlag() | SOUTH_EAST.getFlag();

	public static final int ALL = (1 << values().length) - 1;

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

	public Direction roundedToCorners() {
		int column = in(RIGHT) ? 2 : 0;
		int row = in(BOTTOM) ? 2 : 0;

		return get(row, column);
	}

	public int getColumn() {return ordinal() % 3;}
	public int getRow() {return ordinal() / 3;}

	public Direction withColumn(int column) {return get(getRow(), column);}
	public Direction withRow(int row) {return get(row, getColumn());}

	public Direction mirrorRow() {return get(2 - getRow(), getColumn());}
	public Direction mirrorColumn() {return get(getRow(), 2 - getColumn());}
	public Direction mirror() {return get(2 - getRow(), 2 - getColumn());}

	/** @see #in(int) */
	public static boolean in(Direction direction, int flags) {
		return direction != null && direction.in(flags);
	}

	public boolean in(int flags) {
		return (flags & getFlag()) != 0;
	}

	public int getFlag() {
		return 1 << ordinal();
	}

	public static int getFlags(Direction... directions) {
		int flags = 0;

		for(Direction direction : directions) {
			flags |= direction.getFlag();
		}
		return flags;
	}

	/** @return The anchor point between zero and {@code size}
	 * corresponding to this direction */
	public Point getAnchor(Point size) {
		return size.scale(getColumn() / 2f, getRow() / 2f);
	}

	/** @return The anchor point within {@code bounds}
	 * corresponding to this direction */
	public Point getAnchor(Bounds bounds) {
		return bounds.getPosition().add(getAnchor(bounds.getSize()));
	}

	/** {@code point} defaults to the north-west corner of {@code bounds}
	 * @see #align(Bounds, Point) */
	public Bounds align(Bounds bounds) {
		return align(bounds, bounds.getPosition());
	}

	/** Aligns the side(s) of {@code bounds} around {@code point},
	 * for example {@link EAST} aligns the east edge of {@code bounds} to {@code point} */
	public Bounds align(Bounds bounds, Point point) {
		return bounds.withPosition(point.sub(getAnchor(bounds.getSize())));
	}

	/** Aligns the side(s) of {@code bounds} to the side(s) of {@code container} */
	public Bounds anchor(Bounds bounds, Bounds container) {
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
