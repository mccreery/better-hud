package tk.nukeduck.hud.element.settings;

import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.Gui;
import tk.nukeduck.hud.gui.GuiToggleButton;

@Deprecated
public class SettingBooleanLeft extends SettingBoolean {
	public SettingBooleanLeft(String name) {
		super(name);
	}
	
	@Override
	public int getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, int width, int y) {
		parts.add(toggler = new GuiToggleButton(0, width / 2 - 155, y, 150, 20, getUnlocalizedName(), true));
		return -1;
	}
}
