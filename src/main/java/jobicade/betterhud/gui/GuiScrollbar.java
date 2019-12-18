package jobicade.betterhud.gui;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.util.math.MathHelper;

public class GuiScrollbar extends Gui implements IGuiEventListener {
	private final Rect bounds;
	private Rect grabber;
	private float scaleFactor;
	private final Color background, foreground, highlight;

	/** The size of the visible part of the content */
	private int viewportHeight;

	/** The total size of the content */
	private int contentHeight;

	/** The offset of the mouse click from the least coordinate of the grabber */
	private int clickOffset = -1;

	/** The difference between the least coordinate of {@link #viewportHeight} and that of of the content */
	private int scroll;

	/** {@code bounds} defaults to a bar anchored to {@code viewport},
	 * {@code viewport} defaults to the height of {@code viewport},
	 * the colors used are defaults
	 *
	 * @see #GuiScrollbar(Rect, int, int, int, int, int) */
	public GuiScrollbar(Rect viewport, int content) {
		this(new Rect(8, viewport.getHeight()).anchor(viewport, Direction.NORTH_EAST), viewport.getHeight(), content);
	}

	/** The colors used are defaults
	 * @see #GuiScrollbar(Rect, int, int, int, int, int) */
	public GuiScrollbar(Rect bounds, int viewport, int content) {
		this(bounds, viewport, content, Color.TRANSLUCENT, Color.FOREGROUND, Color.HIGHLIGHT);
	}

	/** @param bounds The rendering bounds for the scrollbar
	 * @param viewport The size of the viewport
	 * @param content The size of the content */
	public GuiScrollbar(Rect bounds, int viewport, int content, Color background, Color foreground, Color highlight) {
		this.bounds = bounds;

		this.viewportHeight = viewport;
		this.contentHeight = content;
		updateGrabber();

		this.background = background;
		this.foreground = foreground;
		this.highlight = highlight;
	}

	public int getScroll() {
		return scroll;
	}

	public int getMaxScroll() {
		return contentHeight - viewportHeight;
	}

	public boolean canScroll() {
		return viewportHeight > 0 && contentHeight > viewportHeight;
	}

	public boolean isScrolling() {
		return clickOffset != -1;
	}

	public void setContentHeight(int contentHeight) {
		if(this.contentHeight != contentHeight) {
			this.contentHeight = contentHeight;
			updateGrabber();
		}
	}

	public void setViewportHeight(int viewportHeight) {
		if(this.viewportHeight != viewportHeight) {
			this.viewportHeight = viewportHeight;
			updateGrabber();
		}
	}

	public void setScroll(int scroll) {
		if(this.scroll != scroll) {
			this.scroll = scroll;
			updateGrabber();
		}
	}

	private void updateGrabber() {
		if(canScroll()) {
			scaleFactor = (float)viewportHeight / contentHeight;
			scroll = MathHelper.clamp(scroll, 0, contentHeight - viewportHeight);

			grabber = new Rect(bounds.getX(), bounds.getY() + (int)(scroll * scaleFactor),
				bounds.getWidth(), (int)(scaleFactor * bounds.getHeight()));
		}
	}

	public void drawScrollbar(int mouseX, int mouseY) {
		GlUtil.drawRect(bounds, background);

		if(canScroll()) {
			Color grabColor = isScrolling() || bounds.contains(mouseX, mouseY) ? highlight : foreground;
			GlUtil.drawRect(grabber, grabColor);
		}
	}

	@Override
	public boolean mouseScrolled(double delta) {
		if(canScroll() && !isScrolling() && delta != 0) {
			setScroll(scroll - delta > 0 ? 20 : -20);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int modifiers) {
		if(canScroll() && !isScrolling() && bounds.contains((int)mouseX, (int)mouseY)) {
			if(grabber.contains((int)mouseX, (int)mouseY)) {
				clickOffset = (int)mouseY - grabber.getTop();
			} else {
				clickOffset = grabber.getHeight() / 2;
			}

			//mouseClickMove(mouseX, mouseY, button, 0);
		}
		return false;//
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int modifiers,
			double p_mouseDragged_6_, double p_mouseDragged_8_) {
		if(isScrolling()) {
			setScroll((int)((mouseY - bounds.getY() - clickOffset) / scaleFactor));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int modifiers) {
		clickOffset = -1;
		return true;
	}
}
