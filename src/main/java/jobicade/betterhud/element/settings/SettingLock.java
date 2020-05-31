package jobicade.betterhud.element.settings;

import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import net.minecraft.client.gui.Gui;
import jobicade.betterhud.gui.GuiTexturedButton;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Point;

public class SettingLock extends SettingBoolean {
	private Rect bounds;

	public SettingLock(String name) {
		super(name);
	}

	public void setRect(Rect bounds) {
		this.bounds = bounds;
	}

	@Override
	public void getGuiParts(List<Gui> parts, Map<Gui, Setting<?, ?>> callbacks, Rect bounds) {
		toggler = new GuiTexturedButton(new Rect(0, 60, 20, 10)).setBounds(bounds).setCallback(b -> toggle());
		parts.add(toggler);
		callbacks.put(toggler, this);
	}

	@Override
	public Point getGuiParts(List<Gui> parts, Map<Gui, Setting<?, ?>> callbacks, Point origin) {
		getGuiParts(parts, callbacks, bounds);
		return null;
	}

	@Override
	public SettingLock setEnableOn(BooleanSupplier enableOn) {
		super.setEnableOn(enableOn);
		return this;
	}
}
