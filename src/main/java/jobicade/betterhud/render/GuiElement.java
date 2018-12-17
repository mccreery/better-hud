package jobicade.betterhud.render;

import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;

/**
 * Represents something which can be rendered on-screen.
 * Instances have a set size and can be moved around.
 */
public abstract class GuiElement {
	/**
	 * The outer bounding box for rendering.
	 */
	protected Rect bounds;

	/**
	 * Constructor for elements. The position defaults to zero.
	 * @param size The size of the bounding box.
	 */
	public GuiElement(Point size) {
		this(new Rect(size));
	}

	/**
	 * Constructor for elements.
	 * @param bounds The initial outer bounding box.
	 */
	public GuiElement(Rect bounds) {
		this.bounds = bounds;
	}

	/**
	 * Getter for bounds.
	 * @return The outer bounding box of the element.
	 */
	public Rect getBounds() {
		return bounds;
	}

	/**
	 * Moves the outer bounding box to a new position. Size does not change.
	 * @param position The new position of the bounding box.
	 */
	public void move(Point position) {
		bounds = bounds.move(position);
	}

	/**
	 * Renders the element. All rendering should be done inside the bounding box.
	 * @see #getBounds()
	 */
	public abstract void render();
}
