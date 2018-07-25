package tk.nukeduck.hud.gui;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.gui.GuiScreen;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.util.Bounds;

class GuiOptionButton extends GuiTexturedButton {
	private final GuiScreen parent;
	private final HudElement element;

	GuiOptionButton(GuiScreen parent, HudElement element) {
		super(new Bounds(40, 0, 20, 20));

		this.parent  = parent;
		this.element = element;
	}

	@Override
	public void actionPerformed() {
		MC.displayGuiScreen(new GuiElementSettings(element, parent));
	}
}
