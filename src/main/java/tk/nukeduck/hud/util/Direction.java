package tk.nukeduck.hud.util;

import java.util.function.Function;

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

	public enum Options implements Function<Direction, Direction> {
		ALL((1 << Direction.values().length) - 1) {
			@Override
			public Direction apply(Direction direction) {
				return direction != null ? direction : NORTH_WEST;
			}
		},
		CORNERS(NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST) {
			@Override
			public Direction apply(Direction direction) {
				if(direction != null) {
					return fromRowColumn(direction.getRow() > 1 ? 2 : 0, direction.getColumn() > 1 ? 2 : 0);
				} else {
					return NORTH_WEST;
				}
			}
		},
		WEST_EAST(WEST, EAST) {
			@Override
			public Direction apply(Direction direction) {
				return direction != null && direction.getColumn() >= 2 ? EAST : WEST;
			}
		},
		NORTH_SOUTH(NORTH, SOUTH) {
			@Override
			public Direction apply(Direction direction) {
				return direction != null && direction.getRow() >= 2 ? SOUTH : NORTH;
			}
		},
		HORIZONTAL(WEST, CENTER, EAST) {
			@Override
			public Direction apply(Direction direction) {
				return direction != null ? direction.withRow(1) : WEST;
			}
		},
		VERTICAL(NORTH, CENTER, SOUTH) {
			@Override
			public Direction apply(Direction direction) {
				return direction != null ? direction.withColumn(1) : NORTH;
			}
		},
		I(NORTH_WEST, NORTH, NORTH_EAST, CENTER, SOUTH_WEST, SOUTH, SOUTH_EAST) {
			@Override
			public Direction apply(Direction direction) {
				switch(direction) {
					case WEST: return NORTH_WEST;
					case EAST: return NORTH_EAST;
					default:   return direction != null ? direction : NORTH_WEST;
				}
			}
		},
		BAR(NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH, SOUTH_EAST) {
			@Override
			public Direction apply(Direction direction) {
				if(direction == SOUTH) {
					return direction;
				} else {
					return CORNERS.apply(direction);
				}
			}
		},
		X(NORTH_WEST, NORTH_EAST, CENTER, SOUTH_WEST, SOUTH_EAST) {
			@Override
			public Direction apply(Direction direction) {
				return direction == CENTER ? direction : CORNERS.apply(direction);
			}
		},
		TOP_BOTTOM(NORTH_WEST, NORTH, NORTH_EAST, SOUTH_WEST, SOUTH, SOUTH_EAST) {
			@Override
			public Direction apply(Direction direction) {
				if(direction == null) {
					return NORTH_WEST;
				} else if(direction.getRow() == 1) {
					return direction.withRow(0);
				} else {
					return direction;
				}
			}
		},
		NONE() {
			@Override
			public Direction apply(Direction direction) {
				return null;
			}
		};

		private final int flags;

		Options(Direction... directions) {
			this(getFlags(directions));
		}
		Options(int flags) {
			this.flags = flags;
		}

		public boolean isValid(Direction direction) {
			return in(direction, flags);
		}
	}

	private final String name;

	Direction(String name) {
		this.name = name;
	}

	public String getUnlocalizedName() {
		return "betterHud.value." + name;
	}

	public String getLocalizedName() {
		return I18n.format(getUnlocalizedName());
	}

	public int getRow() {return ordinal() / 3;}
	public int getColumn() {return ordinal() % 3;}

	public Direction withColumn(int column) {return fromRowColumn(getRow(), column);}
	public Direction withRow(int row) {return fromRowColumn(row, getColumn());}

	public Direction mirrorRow() {return fromRowColumn(2 - getRow(), getColumn());}
	public Direction mirrorColumn() {return fromRowColumn(getRow(), 2 - getColumn());}
	public Direction mirror() {return fromRowColumn(2 - getRow(), 2 - getColumn());}

	/** @see #in(int) */
	private static boolean in(Direction direction, int flags) {
		return direction != null && direction.in(flags);
	}

	private boolean in(int flags) {
		return (flags & getFlag()) != 0;
	}

	private int getFlag() {
		return 1 << ordinal();
	}

	private static int getFlags(Direction... directions) {
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

	public Point getRowColumn() {
		return new Point(getColumn(), getRow());
	}
	public static Direction fromRowColumn(Point rowColumn) {
		return fromRowColumn(rowColumn.getY(), rowColumn.getX());
	}
	public static Direction fromRowColumn(int row, int column) {
		return values()[row * 3 + column];
	}

	public static String toString(Direction direction) {
		return direction != null ? direction.toString() : "";
	}

	@Override
	public String toString() {
		return name;
	}

	public static Direction fromString(String name) {
		for(Direction direction : values()) {
			if(direction.name.equalsIgnoreCase(name)) return direction;
		}
		return null;
	}
}
