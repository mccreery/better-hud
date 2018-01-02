package tk.nukeduck.hud.element.settings;

import static tk.nukeduck.hud.BetterHud.SPACER;

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
		y = super.getGuiParts(parts, callbacks, width, y);
		Bounds bounds;

		if(alignment == Direction.CENTER) {
			bounds = new Bounds(width / 2 - 100, y, 200, 20);
		} else {
			bounds = alignment.anchor(new Bounds(0, 0, 150, 20), new Bounds(width / 2 - 150 - SPACER / 2, y, 300 + SPACER, 20));
		}

		int bottom = getGuiParts(parts, callbacks, bounds);
		return alignment != Direction.WEST ? bottom : -1;
	}

	/** @see Setting#getGuiParts(List, Map, int, int) */
	public abstract int getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Bounds bounds);
}
