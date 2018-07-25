package tk.nukeduck.hud.gui;

import static tk.nukeduck.hud.BetterHud.SPACER;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;

class ButtonRow {
	private final HudElement element;
	private final GuiElementToggle toggle;
	private final GuiActionButton options;
	private final GuiActionButton moveUp, moveDown;

	private Bounds bounds;

	public ButtonRow(GuiHudMenu callback, HudElement element) {
		this.element = element;

		toggle   = new GuiElementToggle(callback, element);
		options  = new GuiOptionButton(callback, element);
		moveUp   = new PriorityButton(callback, element, true);
		moveDown = new PriorityButton(callback, element, false);
	}

	public ButtonRow setBounds(Bounds bounds) {
		this.bounds = bounds;

		toggle.setBounds(bounds.withWidth(bounds.getWidth() - 20).anchoredTo(bounds, Direction.NORTH_WEST));
		options.setBounds(bounds.withWidth(20).anchoredTo(bounds, Direction.NORTH_EAST));

		Bounds arrows = new Bounds(20, 20).anchoredTo(bounds.withPadding(20 + SPACER), Direction.WEST);
		moveUp.setBounds(new Bounds(20, 10).anchoredTo(arrows, Direction.NORTH_WEST));
		moveDown.setBounds(new Bounds(20, 10).anchoredTo(arrows, Direction.SOUTH_WEST));

		return this;
	}

	public Bounds getBounds() {
		return bounds;
	}

	public List<GuiButton> getButtons() {
		return Arrays.asList(toggle, options, moveUp, moveDown);
	}

	public ButtonRow update() {
		boolean supported = element.isSupportedByServer();

		toggle.enabled = supported;
		toggle.updateText();
		options.enabled = supported && toggle.get() && !element.settings.isEmpty();

		return this;
	}
}
