package jobicade.betterhud.element.settings;

import net.minecraft.client.gui.GuiButton;
import jobicade.betterhud.gui.GuiElementSettings;

// TODO I don't like it no sir
/** A default implementation of {@link Setting} which stores no value.<br>
 * It is used for settings which are for display only and which only store
 * the values of their children */
public class SettingStub<T> extends Setting<T, SettingStub<T>> {
	public SettingStub() {
		this(null);
	}

	public SettingStub(String name) {
		super(name);
	}

	@Override public T get() {return null;}
	@Override public void set(T value) {}
	@Override public String getStringValue() {return null;}
	@Override public void loadStringValue(String save) {}
	@Override public void loadDefaultValue() {}
	@Override public void actionPerformed(GuiElementSettings gui, GuiButton button) {}
	@Override protected boolean hasValue() {return false;}

	@Override
	protected SettingStub<T> getThis() {
		return this;
	}
}
