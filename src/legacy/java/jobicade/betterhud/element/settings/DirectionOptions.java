package jobicade.betterhud.element.settings;

import jobicade.betterhud.geom.Direction;
import static jobicade.betterhud.geom.Direction.*;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

public enum DirectionOptions implements Function<Direction, Direction> {
    ALL(Direction.values()) {
        @Override
        public Direction apply(Direction direction) {
            return direction != null ? direction : NORTH_WEST;
        }
    },
    CORNERS(NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST) {
        @Override
        public Direction apply(Direction direction) {
            if(direction != null) {
                return Direction.get(direction.getRow() > 1 ? 2 : 0, direction.getCol() > 1 ? 2 : 0);
            } else {
                return NORTH_WEST;
            }
        }
    },
    WEST_EAST(WEST, EAST) {
        @Override
        public Direction apply(Direction direction) {
            return direction != null && direction.getCol() >= 2 ? EAST : WEST;
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
            return direction != null ? direction.withCol(1) : NORTH;
        }
    },
    I(NORTH_WEST, NORTH, NORTH_EAST, CENTER, SOUTH_WEST, SOUTH, SOUTH_EAST) {
        @Override
        public Direction apply(Direction direction) {
            if (direction == null || direction == WEST) {
                return NORTH_WEST;
            } else if (direction == EAST) {
                return NORTH_EAST;
            } else {
                return direction;
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
    LEFT_RIGHT(NORTH_WEST, WEST, SOUTH_WEST, NORTH_EAST, EAST, SOUTH_EAST) {
        @Override
        public Direction apply(Direction direction) {
            if(direction == null) {
                return NORTH_WEST;
            } else if(direction.getCol() == 1) {
                return direction.withCol(0);
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

    private final Set<Direction> directions;

    DirectionOptions(Direction... directions) {
        this.directions = EnumSet.noneOf(Direction.class);
        Collections.addAll(this.directions, directions);
    }

    public boolean isValid(Direction direction) {
        return directions.contains(direction);
    }
}
