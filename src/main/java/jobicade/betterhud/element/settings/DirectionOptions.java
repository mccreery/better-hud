package jobicade.betterhud.element.settings;

import static jobicade.betterhud.geom.Direction.CENTER;
import static jobicade.betterhud.geom.Direction.EAST;
import static jobicade.betterhud.geom.Direction.NORTH;
import static jobicade.betterhud.geom.Direction.NORTH_EAST;
import static jobicade.betterhud.geom.Direction.NORTH_WEST;
import static jobicade.betterhud.geom.Direction.SOUTH;
import static jobicade.betterhud.geom.Direction.SOUTH_EAST;
import static jobicade.betterhud.geom.Direction.SOUTH_WEST;
import static jobicade.betterhud.geom.Direction.WEST;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import jobicade.betterhud.geom.Direction;

public enum DirectionOptions {
    ALL(Direction.values()),
    CORNERS(NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST),
    WEST_EAST(WEST, EAST),
    NORTH_SOUTH(NORTH, SOUTH),
    HORIZONTAL(WEST, CENTER, EAST),
    VERTICAL(NORTH, CENTER, SOUTH),
    I(NORTH_WEST, NORTH, NORTH_EAST, CENTER, SOUTH_WEST, SOUTH, SOUTH_EAST),
    BAR(NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH, SOUTH_EAST),
    X(NORTH_WEST, NORTH_EAST, CENTER, SOUTH_WEST, SOUTH_EAST),
    TOP_BOTTOM(NORTH_WEST, NORTH, NORTH_EAST, SOUTH_WEST, SOUTH, SOUTH_EAST),
    LEFT_RIGHT(NORTH_WEST, WEST, SOUTH_WEST, NORTH_EAST, EAST, SOUTH_EAST),
    NONE();

    private final Set<Direction> directions;

    DirectionOptions(Direction... directions) {
        this.directions = EnumSet.noneOf(Direction.class);
        Collections.addAll(this.directions, directions);
    }

    public boolean isValid(Direction direction) {
        return directions.contains(direction);
    }

    private static final int[][] closestOrder = {
        {0, 1, 2},
        {1, 0, 2},
        {2, 1, 0}
    };

    public Direction apply(Direction direction) {
        if (directions.isEmpty()) {
            return null;
        } else if (direction == null) {
            return directions.iterator().next();
        }

        for (int row : closestOrder[direction.getRow()]) {
            for (int col : closestOrder[direction.getCol()]) {
                Direction option = Direction.get(row, col);

                if (directions.contains(option)) {
                    return option;
                }
            }
        }
        return null;
    }
}
