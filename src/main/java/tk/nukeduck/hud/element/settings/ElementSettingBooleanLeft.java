package tk.nukeduck.hud.element.settings;

import java.io.IOException;
import java.util.Collection;

import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.util.FormatUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class ElementSettingBooleanLeft extends ElementSettingBoolean {
	public ElementSettingBooleanLeft(String name) {
		super(name);
	}
	
	public int getGuiHeight() {
		return -5;
	}
	
	@Override
	public Gui[] getGuiParts(int width, int y) {
		toggler = new GuiButton(0, width / 2 - 155, y, 150, 20, FormatUtil.translatePre("menu.settingButton", this.getLocalizedName(), FormatUtil.translate(value ? "options.on" : "options.off")));
		return new Gui[] {toggler};
	}
}