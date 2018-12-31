package jobicade.betterhud.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;

public class Grid implements Boxed {
    private List<Boxed> source;
    private Rect shape;

    private Size gutter = Size.zero();
    private boolean stretch = false;
    private Direction alignment = Direction.NORTH_WEST;

    public Grid(Point shape) {
        this(shape, new ArrayList<>(Collections.nCopies(shape.getX() * shape.getY(), null)));
    }

    public Grid(Point shape, List<Boxed> source) {
        super();
        this.shape = new Rect(shape);
        this.source = source;
    }

    public Grid setSource(List<Boxed> source) {
        this.source = source;
        return this;
    }

    public List<Boxed> getSource() {
        return source;
    }

    public List<Boxed> flatten() {
        return source.subList(0, shape.getWidth() * shape.getHeight());
    }

    public Boxed getCell(Point position) {
        return source.get(getCellIndex(position));
    }

    public Grid setCell(Point position, Boxed element) {
        source.set(getCellIndex(position), element);
        return this;
    }

    private int getCellIndex(Point position) {
        if(!shape.contains(position)) {
            throw new IndexOutOfBoundsException("Grid coordinates " + position);
        }
        return position.getY() * shape.getWidth() + position.getX();
    }

    public Size getGutter() {
        return gutter;
    }

    public Grid setGutter(Point gutter) {
        this.gutter = gutter instanceof Size ? (Size)gutter : new Size(gutter);
        return this;
    }

    public boolean hasStretch() {
        return stretch;
    }

    public Grid setStretch(boolean stretch) {
        this.stretch = stretch;
        return this;
    }

    public Direction getAlignment() {
        return alignment;
    }

    public Grid setAlignment(Direction alignment) {
        if(!DirectionOptions.CORNERS.isValid(alignment)) {
            throw new IllegalArgumentException("Grid alignment must be a corner");
        }
        this.alignment = alignment;
        return this;
    }

    @Override
    public Size getPreferredSize() {
        int width = 0, height = 0;

        for(Boxed element : flatten()) {
            if(element == null) continue;
            Size size = element.getPreferredSize();

            if(size.getWidth() > width) width = size.getWidth();
            if(size.getHeight() > height) height = size.getHeight();
        }

        return shape.getSize().scale(width, height)
            .add(shape.getSize().sub(1, 1).scale(gutter));
    }

    @Override
    public void render(Rect bounds) {
        int x = 0;

        Size gutterless = bounds.getSize().sub(shape.getSize().sub(1, 1).scale(gutter));
        Size cellSize = gutterless.scale(1.0f / shape.getWidth(), 1.0f / shape.getHeight());

        Rect cellLeft = new Rect(cellSize).anchor(bounds, alignment);
        Rect cell = cellLeft;

        Rect gutterPadding = new Rect(gutter.invert(), new Point(gutter));
        Direction flow = alignment.mirror();

        for(Boxed element : flatten()) {
            if(element != null) {
                Size size = stretch ? element.negotiateSize(cell.getSize()) : element.getPreferredSize();
                element.render(new Rect(size).anchor(cell, Direction.CENTER));
            }

            if(x >= shape.getWidth() - 1) {
                x = 0;
                cellLeft = cellLeft.anchor(cellLeft.grow(gutterPadding), flow.withCol(1), true);
                cell = cellLeft;
            } else {
                ++x;
                cell = cell.anchor(cell.grow(gutterPadding), flow.withRow(1), true);
            }
        }
    }
}
