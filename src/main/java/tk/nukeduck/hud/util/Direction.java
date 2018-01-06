package tk.nukeduck.hud.util;

/** One of 8 cardinal directions or {@link CENTER}, the null direction */
public enum Direction {
	// TODO easier way to manipulate directions, like mirroring etc
	CENTER(.5f, .5f, "center"),

	NORTH(.5f,  0f, "north"),
	EAST ( 1f, .5f, "east"),
	SOUTH(.5f,  1f, "south"),
	WEST ( 0f, .5f, "west"),

	NORTH_EAST(1f,  0f, "northEast"),
	SOUTH_EAST(1f,  1f, "southEast"),
	SOUTH_WEST(0f,  1f, "southWest"),
	NORTH_WEST(0f,  0f, "northWest");

	public static final int CORNERS    = flags(NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST);
	public static final int SIDES      = flags(NORTH, EAST, SOUTH, WEST);
	public static final int TOP        = flags(NORTH_WEST, NORTH, NORTH_EAST);
	public static final int HORIZONTAL = flags(WEST, CENTER, EAST);
	public static final int BOTTOM     = flags(SOUTH_WEST, SOUTH, SOUTH_EAST);
	public static final int LEFT       = flags(NORTH_WEST, WEST, SOUTH_WEST);
	public static final int VERTICAL   = flags(NORTH, CENTER, SOUTH);
	public static final int RIGHT      = flags(NORTH_EAST, EAST, SOUTH_EAST);
	public static final int ALL        = TOP | HORIZONTAL | BOTTOM;

	private final float scaleX, scaleY;
	private final String name;

	Direction(float x, float y, String name) {
		scaleX = x;
		scaleY = y;
		this.name = name;
	}

	public String getUnlocalizedName() {
		return name;
	}

	public int flag() {
		return 1 << ordinal();
	}

	public static int flags(Direction... directions) {
		int flags = 0;

		for(Direction direction : directions) {
			flags |= direction.flag();
		}
		return flags;
	}

	public boolean in(int flags) {
		return (flags & flag()) != 0;
	}

	/** @return The anchor point between zero and {@code size}
	 * corresponding to this direction */
	public Point getAnchor(Point size) {
		return size.scale(scaleX, scaleY);
	}

	/** @return The anchor point within {@code bounds}
	 * corresponding to this direction */
	public Point getAnchor(Bounds bounds) {
		return bounds.position.add(getAnchor(bounds.size));
	}

	/** Aligns the side(s) of {@code bounds} around its original position
	 * for example {@link EAST} aligns the east edge of
	 * {@code bounds} to its previous north-west corner */
	public <T extends Bounds> T align(T bounds) {
		bounds.position = bounds.position.sub(getAnchor(bounds.size));
		return bounds;
	}

	/** Aligns the side(s) of {@code bounds} to the side(s) of {@code container} */
	public <T extends Bounds> T anchor(T bounds, Bounds container) {
		bounds.position = getAnchor(container);
		return align(bounds);
	}

	public static Direction fromUnlocalizedName(String name) {
		for(Direction direction : values()) {
			if(direction.name.equals(name)) return direction;
		}
		return Direction.NORTH_WEST;
	}
}
