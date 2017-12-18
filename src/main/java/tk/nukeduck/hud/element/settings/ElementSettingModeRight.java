package tk.nukeduck.hud.element.settings;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

public class ElementSettingModeRight extends ElementSettingMode {
	public ElementSettingModeRight(String name, String[] modes) {
		super(name, modes);
	}
	
	@Override
	public Gui[] getGuiParts(int width, int y) {
		backing = new GuiButton(2, width / 2 + 5, y, 150, 20, "");
		last = new GuiButton(0, width / 2 + 5, y, 20, 20, "<");
		next = new GuiButton(1, width / 2 + 135, y, 20, 20, ">");
		backing.enabled = false;
		return new Gui[] {backing, last, next};
	}
}