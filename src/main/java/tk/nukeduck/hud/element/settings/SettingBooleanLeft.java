package tk.nukeduck.hud.element.settings;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class SettingBooleanLeft extends SettingBoolean {
	public SettingBooleanLeft(String name) {
		super(name);
	}
	
	public int getGuiHeight() {
		return -5;
	}
	
	@Override
	public Gui[] getGuiParts(int width, int y) {
		final String text = I18n.format("betterHud.menu.settingButton", this.getLocalizedName(), I18n.format(this.value ? "options.on" : "options.off"));
		toggler = new GuiButton(0, width / 2 - 155, y, 150, 20, text);
		return new Gui[] {toggler};
	}
}