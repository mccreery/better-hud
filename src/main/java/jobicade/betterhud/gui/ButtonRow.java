package jobicade.betterhud.gui;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.HudElement.SortType;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.registry.HudElements;
import jobicade.betterhud.geom.Direction;

class ButtonRow {
	private final HudElement<?> element;
	private final GuiActionButton toggle;
	private final GuiActionButton options;

	private Rect bounds;

	public ButtonRow(GuiScreen callback, HudElement<?> element) {
		this.element = element;

		toggle = new GuiActionButton("").setCallback(b -> {
			element.setEnabled(!element.isEnabled());
			HudElements.get().invalidateSorts(SortType.ENABLED);

			if(Minecraft.getMinecraft().currentScreen != null) {
				Minecraft.getMinecraft().currentScreen.initGui();
			}
		});
		options = new GuiTexturedButton(new Rect(40, 0, 20, 20)).setCallback(b ->
			Minecraft.getMinecraft().displayGuiScreen(new GuiElementSettings(element, callback)));
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
		boolean supported = element.getServerDependency()
			.containsVersion(BetterHud.getServerVersion());

		toggle.enabled = supported;
		toggle.glowing = element.isEnabled();
		toggle.updateText(element.getUnlocalizedName(), "options", element.isEnabled());
		toggle.setTooltip(toggle.enabled ? null : I18n.format("betterHud.menu.unsupported"));

		options.enabled = supported && element.isEnabled() && !element.settings.isEmpty();

		return this;
	}
}
