package tk.nukeduck.hud.gui;

import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.MathHelper;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;

public class GuiScrollbar extends Gui {
	private final Bounds bounds;
	private Bounds grabber;
	private float scaleFactor;
	private final int background, foreground, highlight;

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
	 * @see #GuiScrollbar(Bounds, int, int, int, int, int) */
	public GuiScrollbar(Bounds viewport, int content) {
		this(new Bounds(8, viewport.getHeight()).anchoredTo(viewport, Direction.NORTH_EAST), viewport.getHeight(), content);
	}

	/** The colors used are defaults
	 * @see #GuiScrollbar(Bounds, int, int, int, int, int) */
	public GuiScrollbar(Bounds bounds, int viewport, int content) {
		this(bounds, viewport, content, Colors.TRANSLUCENT, Colors.FOREGROUND, Colors.HIGHLIGHT);
	}

	/** @param bounds The rendering bounds for the scrollbar
	 * @param viewport The size of the viewport
	 * @param content The size of the content */
	public GuiScrollbar(Bounds bounds, int viewport, int content, int background, int foreground, int highlight) {
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

			grabber = new Bounds(bounds.getX(), bounds.getY() + (int)(scroll * scaleFactor),
				bounds.getWidth(), (int)(scaleFactor * bounds.getHeight()));
		}
	}

	public void drawScrollbar(int mouseX, int mouseY) {
		GlUtil.drawRect(bounds, background);

		if(canScroll()) {
			int grabColor = isScrolling() || bounds.contains(mouseX, mouseY) ? highlight : foreground;
			GlUtil.drawRect(grabber, grabColor);
		}
	}

	public void handleMouseInput() {
		int scrollDelta = Mouse.getEventDWheel();

		if(canScroll() && !isScrolling() && scrollDelta != 0) {
			setScroll(scroll - (scrollDelta > 0 ? 20 : -20));
		}
	}

	protected void mouseClicked(int mouseX, int mouseY, int button) {
		if(canScroll() && !isScrolling() && bounds.contains(mouseX, mouseY)) {
			if(grabber.contains(mouseX, mouseY)) {
				clickOffset = mouseY - grabber.getTop();
			} else {
				clickOffset = grabber.getHeight() / 2;
			}

			mouseClickMove(mouseX, mouseY, button, 0);
		}
	}

	protected void mouseClickMove(int mouseX, int mouseY, int button, long heldTime) {
		if(isScrolling()) {
			setScroll((int)((mouseY - bounds.getY() - clickOffset) / scaleFactor));
		}
	}

	public void mouseReleased(int mouseX, int mouseY, int button) {
		clickOffset = -1;
	}
}
