package tk.nukeduck.hud.element.settings;

import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.Gui;

public class RootSetting extends SettingBoolean {
	public RootSetting() {
		super("enabled");
	}

	/** @see Setting#getGuiParts(List, Map, int, int) */
	@Override
	public int getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, int width, int y) {
		return getGuiParts(parts, callbacks, width, y, children);
	}
}
