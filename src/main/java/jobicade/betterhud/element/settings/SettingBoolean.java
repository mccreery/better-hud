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
import net.minecraftforge.common.config.Property.Type;

public class SettingBoolean extends SettingAlignable<Boolean> {
	public static final String VISIBLE = "betterHud.value.visible";

	protected GuiActionButton toggler;
	protected boolean value = false;

	private String unlocalizedValue = "options";

	public SettingBoolean(String name) {
		this(name, Direction.CENTER);
	}

	public SettingBoolean(String name, Direction alignment) {
		super(name, alignment);
	}

	public SettingBoolean setValuePrefix(String value) {
		this.unlocalizedValue = value;
		return this;
	}

	@Override
	protected Type getPropertyType() {
		return Type.BOOLEAN;
	}

	@Override
	public Boolean get() {
		// TODO does it make sense to test enabled here?
		return enabled() && value;
	}

	@Override
	public void set(Boolean value) {
		this.value = value;
		//return true;
	}

	@Override
	public void getGuiParts(List<Gui> parts, Map<Gui, Setting<?>> callbacks, Rect bounds) {
		toggler = new GuiActionButton("").setBounds(bounds).setCallback(b -> value = !value);
		parts.add(toggler);
		callbacks.put(toggler, this);
	}

	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		toggler.actionPerformed();
	}

	@Override
	public String getStringValue() {
		return String.valueOf(value);
	}

	@Override
	public String getDefaultValue() {
		return "false";
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
	public void loadDefaultValue() {
		value = false;
	}

	@Override
	public void updateGuiParts(Collection<Setting<?>> settings) {
		super.updateGuiParts(settings);
		toggler.enabled = enabled();
		toggler.updateText(getUnlocalizedName(), unlocalizedValue, get());
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
