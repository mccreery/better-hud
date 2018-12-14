package jobicade.betterhud.util.geom;

/**
 * Enum representing the eight cardinal directions and the
 * "null" direction, center.
 */
public enum Direction {
	/** The north west direction, equivalent to top left. */
	NORTH_WEST(0, 0),
	/** The north direction, equivalent to top. */
	NORTH(0, 1),
	/** The north east direction, equivalent to top right. */
	NORTH_EAST(0, 2),
	/** The west direction, equivalent to left. */
	WEST(1, 0),
	/** The null center direction. */
	CENTER(1, 1),
	/** The east direction, equivalent to right. */
	EAST(1, 2),
	/** The south west direction, equivalent to bottom left. */
	SOUTH_WEST(2, 0),
	/** The south direction, equivalent to bottom. */
	SOUTH(2, 1),
	/** The south east direction, equivalent to bottom right. */
	SOUTH_EAST(2, 2);

	private final int row, col;
	private static Direction[][] grid = new Direction[3][3];

	static {
		for(Direction d : values()) {
			grid[d.row][d.col] = d;
		}
	}

	Direction(int row, int col) {
		this.row = row;
		this.col = col;
	}

	/**
	 * Returns a point representing a translation based on 1 unit
	 * in this direction. Diagonal directions return a unit both horizontally
	 * and vertically, so the translation is actually sqrt(2).
	 *
	 * @return A point representing a unit translation in this direction.
	 */
	public Point getUnit() {
		return new Point(col - 1, row - 1);
	}

	/**
	 * Returns the direction in the given row and column. The left hand side is
	 * column 0, the right hand side is column 2, the top side is row 0 and
	 * the bottom side is row 2.
	 *
	 * @param row The row.
	 * @param col The column.
	 * @return The direction in the given row and column.
	 */
	public static Direction get(int row, int col) {
		return grid[row][col];
	}

	/**
	 * @return The row of this direction.
	 * @see #get(int, int)
	 */
	public int getRow() { return row; }

	/**
	 * @return The row of this direction.
	 * @see #get(int, int)
	 */
	public int getCol() { return col; }

	/**
	 * Returns the corresponding direction in the same column and the given row.
	 *
	 * @param row The row.
	 * @return The corresponding direction in the same column and the given row.
	 */
	public Direction withRow(int row) { return get(row, col); }

	/**
	 * Returns the corresponding direction in the same row and the given column.
	 *
	 * @param col The column.
	 * @return The corresponding direction in the same row and the given column.
	 */
	public Direction withCol(int col) { return get(row, col); }

	/**
	 * Mirrors this direction's row. For example,
	 * {@link #NORTH} becomes {@link #SOUTH}.
	 *
	 * @return This direction with mirrored row.
	 */
	public Direction mirrorRow() { return get(2 - row, col); }

	/**
	 * Mirrors this direction's column. For example,
	 * {@link #WEST} becomes {@link #EAST}.
	 *
	 * @return This direction with mirrored column.
	 */
	public Direction mirrorCol() { return get(row, 2 - col); }

	/**
	 * Mirrors this direction, both row and column.
	 * @return The mirrored direction.
	 */
	public Direction mirror() { return get(2 - row, 2 - col); }
}
