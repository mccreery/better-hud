package jobicade.betterhud.element.settings;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiActionButton;
import jobicade.betterhud.gui.GuiElementSettings;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

public class SettingBoolean extends SettingAlignable {
	public static final String VISIBLE = "betterHud.value.visible";

	protected GuiActionButton toggler;
	private String unlocalizedValue = "options";

	private boolean value = false;

	public SettingBoolean(String name) {
		this(name, Direction.CENTER);
	}

	public SettingBoolean(String name, Direction alignment) {
		super(name, alignment);
	}

	public boolean get() {
		return value;
	}

	public void set(boolean value) {
		this.value = value;
	}

	public SettingBoolean setValuePrefix(String value) {
		this.unlocalizedValue = value;
		return this;
	}

	@Override
	public void getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Rect bounds) {
		toggler = new GuiActionButton("").setBounds(bounds).setCallback(b -> value = !value);
		parts.add(toggler);
		callbacks.put(toggler, this);
	}

	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		toggler.actionPerformed();
	}

	@Override
	public boolean hasValue() {
		return true;
	}

	@Override
	public String getStringValue() {
		return String.valueOf(value);
	}

	@Override
	public void loadStringValue(String stringValue) {
		stringValue = stringValue.trim();

		if ("true".equalsIgnoreCase(stringValue)) {
			value = true;
			//return true;
		} else if ("false".equalsIgnoreCase(stringValue)) {
			value = false;
			//return true;
		} else {
			//return false;
		}
	}

	@Override
	public void updateGuiParts(Collection<Setting> settings) {
		super.updateGuiParts(settings);
		toggler.enabled = enabled();
		toggler.updateText(getUnlocalizedName(), unlocalizedValue, value);
	}

	@Override
	public SettingBoolean setHidden() {
		super.setHidden();
		return this;
	}

	@Override
	public SettingBoolean setUnlocalizedName(String unlocalizedName) {
		super.setUnlocalizedName(unlocalizedName);
		return this;
	}

	@Override
	public SettingBoolean setEnableOn(BooleanSupplier enableOn) {
		super.setEnableOn(enableOn);
		return this;
	}
}
