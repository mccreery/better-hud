package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.MC;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.HudElement.SortType;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Direction;

class ButtonRow {
	private final HudElement element;
	private final GuiActionButton toggle;
	private final GuiActionButton options;

	private Rect bounds;

	public ButtonRow(GuiScreen callback, HudElement element) {
		this.element = element;

		toggle = new GuiActionButton("").setCallback(b -> {
			element.toggle();
			HudElement.SORTER.markDirty(SortType.ENABLED);

			if(MC.currentScreen != null) {
				MC.currentScreen.initGui();
			}
		});
		options = new GuiTexturedButton(new Rect(40, 0, 20, 20)).setCallback(b ->
			MC.displayGuiScreen(new GuiElementSettings(element, callback)));
	}

	public ButtonRow setBounds(Rect bounds) {
		this.bounds = bounds;
		toggle.setBounds(bounds.withWidth(bounds.getWidth() - 20).anchor(bounds, Direction.NORTH_WEST));
		options.setBounds(bounds.withWidth(20).anchor(bounds, Direction.NORTH_EAST));
		return this;
	}

	public Rect getBounds() {
		return bounds;
	}

	public List<GuiButton> getButtons() {
		return Arrays.asList(toggle, options);
	}

	public ButtonRow update() {
		boolean supported = element.isSupportedByServer();

		toggle.enabled = supported;
		toggle.glowing = element.get();
		toggle.updateText(element.getUnlocalizedName(), "options", element.get());
		toggle.setTooltip(toggle.enabled ? null : I18n.format("betterHud.menu.unsupported"));

		options.enabled = supported && element.get() && !element.settings.isEmpty();

		return this;
	}
}
