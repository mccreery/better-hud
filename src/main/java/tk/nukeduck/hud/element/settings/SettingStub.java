package tk.nukeduck.hud.element.settings;

import java.util.Collection;

import net.minecraft.client.gui.GuiButton;
import tk.nukeduck.hud.gui.GuiElementSettings;

/** A default implementation of {@link Setting} which stores no value.<br>
 * It is used for settings which are for display only and which only store
 * the values of their children */
public class SettingStub<T> extends Setting<T> {
	public SettingStub() {
		this(null);
	}

	public SettingStub(String name) {
		super(name);
	}

	@Override public T get() {return null;}
	@Override public void set(T value) {}
	@Override public String save() {return null;}
	@Override public void load(String save) {}
	@Override public void actionPerformed(GuiElementSettings gui, GuiButton button) {}
	@Override public void updateGuiParts(Collection<Setting<?>> settings) {}
	@Override protected boolean hasValue() {return false;}
}
