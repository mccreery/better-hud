package tk.nukeduck.hud.element.settings;

import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.Gui;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;

public abstract class SettingAlignable extends Setting {
	protected final Direction alignment;

	public SettingAlignable(String name, Direction alignment) {
		super(name);
		this.alignment = alignment;
	}

	@Override
	public int getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, int width, int y) {
		Bounds bounds;

		if(alignment == Direction.CENTER) {
			bounds = new Bounds(width / 2 - 100, y, 200, 20);
		} else {
			bounds = alignment.anchor(new Bounds(0, 0, 150, 20), new Bounds(width / 2 - 100, y, 200, 20));
		}

		getGuiParts(parts, callbacks, bounds);
		return alignment != Direction.WEST ? y + 20 : -1;
	}

	public abstract void getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Bounds bounds);
}
