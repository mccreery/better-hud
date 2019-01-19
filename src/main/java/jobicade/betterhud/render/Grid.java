package jobicade.betterhud.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;

public class Grid<T extends Boxed> extends DefaultBoxed {
    private List<T> source;
    private Rect shape;

    private Size gutter = Size.zero();
    private boolean stretch = false;
    private Direction alignment = Direction.NORTH_WEST;
    private Direction cellAlignment = Direction.CENTER;

    public Grid(Point shape) {
        this(shape, new ArrayList<>(Collections.nCopies(shape.getX() * shape.getY(), null)));
    }

    public Grid(Point shape, List<T> source) {
        super();
        this.shape = new Rect(shape);
        this.source = source;
    }

    public Grid<T> setSource(List<T> source) {
        this.source = source;
        return this;
    }

    public List<T> getSource() {
        return source;
    }

    public List<T> flatten() {
        return source.subList(0, shape.getWidth() * shape.getHeight());
    }

    public T getCell(Point position) {
        return source.get(getCellIndex(position));
    }

    public Grid<T> setCell(Point position, T element) {
        source.set(getCellIndex(position), element);
        return this;
    }

    private int getCellIndex(Point position) {
        if(!shape.contains(position)) {
            throw new IndexOutOfBoundsException("Grid coordinates " + position);
        }
        return position.getY() * shape.getWidth() + position.getX();
    }

    public Rect getCellBounds(Rect bounds, Point position) {
        Size gutterless = bounds.getSize().sub(shape.getSize().sub(1, 1).scale(gutter));
        Size cellSize = gutterless.scale(1.0f / shape.getWidth(), 1.0f / shape.getHeight());

        Direction flow = alignment.mirror();
        Point offset = new Point((flow.getCol() - 1) * position.getX(), (flow.getRow() - 1) * position.getY()).scale(cellSize.add(gutter));
        return new Rect(cellSize).anchor(bounds, alignment).translate(offset);
    }

    public Size getGutter() {
        return gutter;
    }

    public Grid<T> setGutter(Point gutter) {
        this.gutter = gutter instanceof Size ? (Size)gutter : new Size(gutter);
        return this;
    }

    public boolean hasStretch() {
        return stretch;
    }

    public Grid<T> setStretch(boolean stretch) {
        this.stretch = stretch;
        return this;
    }

    public Direction getAlignment() {
        return alignment;
    }

    public Grid<T> setAlignment(Direction alignment) {
        if(!DirectionOptions.CORNERS.isValid(alignment)) {
            throw new IllegalArgumentException("Grid alignment must be a corner");
        }
        this.alignment = alignment;
        return this;
    }

    public Direction getCellAlignment() {
        return cellAlignment;
    }

    public Grid<T> setCellAlignment(Direction cellAlignment) {
        this.cellAlignment = cellAlignment;
        return this;
    }

    @Override
    public Size getPreferredSize() {
        if(shape.isEmpty()) return Size.zero();

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
    public void render() {
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
                element.setBounds(new Rect(size).anchor(cell, cellAlignment)).render();
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
