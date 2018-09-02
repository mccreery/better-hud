package tk.nukeduck.hud.gui;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;

class ButtonRow {
	private final HudElement element;
	private final GuiElementToggle toggle;
	private final GuiActionButton options;

	private Bounds bounds;

	public ButtonRow(GuiHudMenu callback, HudElement element) {
		this.element = element;

		toggle = new GuiElementToggle(callback, element);
		options = new GuiOptionButton(callback, element);
	}

	public ButtonRow setBounds(Bounds bounds) {
		this.bounds = bounds;
		toggle.setBounds(bounds.withWidth(bounds.getWidth() - 20).anchor(bounds, Direction.NORTH_WEST));
		options.setBounds(bounds.withWidth(20).anchor(bounds, Direction.NORTH_EAST));
		return this;
	}

	public Bounds getBounds() {
		return bounds;
	}

	public List<GuiButton> getButtons() {
		return Arrays.asList(toggle, options);
	}

	public ButtonRow update() {
		boolean supported = element.isSupportedByServer();

		toggle.enabled = supported;
		toggle.updateText();
		toggle.setTooltip(toggle.enabled ? null : I18n.format("betterHud.menu.unsupported"));

		options.enabled = supported && toggle.get() && !element.settings.isEmpty();

		return this;
	}
}
