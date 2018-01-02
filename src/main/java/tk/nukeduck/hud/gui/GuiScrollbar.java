package tk.nukeduck.hud.gui;

import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.MathHelper;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;

public class GuiScrollbar extends Gui {
	private final Bounds bounds;
	private final Bounds grabber;
	private final float scaleFactor;
	private final int background, foreground, highlight;

	/** The size of the visible part of the content */
	private final int viewport;

	/** The total size of the content */
	private final int content;

	/** The offset of the mouse click from the least coordinate of the grabber */
	private int clickOffset = -1;

	/** The difference between the least coordinate of {@link #viewport} and that of of the content */
	private int scroll;

	/** {@code bounds} defaults to a bar anchored to {@code viewport},
	 * {@code viewport} defaults to the height of {@code viewport},
	 * the colors used are defaults
	 *
	 * @see #GuiScrollbar(Bounds, int, int, int, int, int) */
	public GuiScrollbar(Bounds viewport, int content) {
		this(Direction.NORTH_EAST.anchor(new Bounds(8, viewport.height()), viewport), viewport.height(), content);
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
		this.viewport = viewport;
		this.content = content;
		this.bounds = bounds;

		scaleFactor = (float)viewport / content;
		grabber = new Bounds(bounds.x(), bounds.y(), bounds.width(), (int)(scaleFactor * bounds.height()));

		this.background = background;
		this.foreground = foreground;
		this.highlight = highlight;
	}

	public void scrollTo(int scroll) {
		this.scroll = MathHelper.clamp(scroll, 0, maxScroll());
		grabber.y(bounds.y() + (int)(this.scroll * scaleFactor));
	}

	public void drawScrollbar(int mouseX, int mouseY) {
		HudElement.drawRect(bounds, background);

		if(canScroll()) {
			int grabColor = isScrolling() || bounds.contains(mouseX, mouseY) ? highlight : foreground;
			HudElement.drawRect(grabber, grabColor);
		}
	}

	public void handleMouseInput() {
		if(canScroll() && !isScrolling() && Mouse.getEventDWheel() != 0) {
			int dScroll = Mouse.getEventDWheel() > 0 ? -20 : 20;

			if(dScroll != 0) {
				scrollTo(scroll + dScroll);
			}
		}
	}

	protected void mouseClicked(int mouseX, int mouseY, int button) {
		if(canScroll() && !isScrolling() && bounds.contains(mouseX, mouseY)) {
			if(grabber.contains(mouseX, mouseY)) {
				clickOffset = mouseY - grabber.top();
			} else {
				clickOffset = grabber.height() / 2;
			}

			mouseClickMove(mouseX, mouseY, button, 0);
		}
	}

	protected void mouseClickMove(int mouseX, int mouseY, int button, long heldTime) {
		if(isScrolling()) {
			scrollTo((int)((mouseY - bounds.y() - clickOffset) / scaleFactor));
		}
	}

	public void mouseReleased(int mouseX, int mouseY, int button) {
		clickOffset = -1;
	}

	public int getScroll() {return scroll;}
	public int maxScroll() {return content - viewport;}
	public boolean canScroll() {return maxScroll() > 0;}
	public boolean isScrolling() {return clickOffset != -1;}
}
