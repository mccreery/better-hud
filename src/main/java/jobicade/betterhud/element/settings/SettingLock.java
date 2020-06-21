package jobicade.betterhud.element.settings;

import java.util.List;
import java.util.Map;

import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiTexturedButton;
import net.minecraft.client.gui.Gui;

public class SettingLock extends SettingBoolean {
	private Rect bounds;

	private SettingLock(SettingBoolean.Builder<?> builder) {
		super(builder);
	}

	public void setRect(Rect bounds) {
		this.bounds = bounds;
	}

	@Override
	public void getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Rect bounds) {
		toggler = new GuiTexturedButton(new Rect(0, 60, 20, 10)).setBounds(bounds).setCallback(b -> set(!get()));
		parts.add(toggler);
		callbacks.put(toggler, this);
	}

	@Override
	public Point getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Point origin) {
		getGuiParts(parts, callbacks, bounds);
		return origin;
	}

	public static SettingBoolean.Builder<? extends SettingLock> builder(String name) {
		return new SettingBoolean.Builder<SettingLock>(name, SettingLock::new);
	}
}
