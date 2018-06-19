package tk.nukeduck.hud.element.settings;

import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.Gui;
import tk.nukeduck.hud.gui.GuiLockToggle;
import tk.nukeduck.hud.gui.GuiToggleButton;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Point;

public class SettingLock extends SettingBoolean {
	private Bounds bounds;

	public SettingLock(String name) {
		super(name);
	}

	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	@Override
	public void getGuiParts(List<Gui> parts, Map<Gui, Setting<?>> callbacks, Bounds bounds) {
		toggler = (GuiToggleButton)new GuiLockToggle(this).setBounds(bounds);
		parts.add(toggler);
		callbacks.put(toggler, this);
	}

	@Override
	public Point getGuiParts(List<Gui> parts, Map<Gui, Setting<?>> callbacks, Point origin) {
		getGuiParts(parts, callbacks, bounds);
		return null;
	}
}
