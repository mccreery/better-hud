package jobicade.betterhud.render;

import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;

/**
 * A {@link GuiElement} which can also be resized.
 * @see GuiElement
 */
public abstract class GuiResizable extends GuiElement {
    /**
     * Constructor for resizables.
     * @see GuiElement#GuiElement(Point)
     */
    public GuiResizable(Point size) {
        super(size);
    }

    /**
     * Constructor for resizables.
     * @see GuiElement#GuiElement(Rect)
     */
    public GuiResizable(Rect bounds) {
        super(bounds);
    }

    /**
     * Sets the bounding box to a new rectangle.
     * @param bounds The new bounding box.
     */
    public GuiResizable setBounds(Rect bounds) {
        this.bounds = bounds;
        return this;
    }
}
