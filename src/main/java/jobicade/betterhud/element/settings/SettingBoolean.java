package jobicade.betterhud.element.settings;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiActionButton;
import jobicade.betterhud.gui.GuiElementSettings;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

public class SettingBoolean extends SettingAlignable implements IStringSetting {
	public static final String VISIBLE = "betterHud.value.visible";

	protected GuiActionButton toggler;
	private final String unlocalizedValue;

	private boolean value = false;

	protected SettingBoolean(Builder<?> builder) {
		super(builder);
		unlocalizedValue = builder.unlocalizedValue;
	}

	public boolean get() {
		return value;
	}

	public void set(boolean value) {
		this.value = value;
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
	public IStringSetting getStringSetting() {
		return this;
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
	public void updateGuiParts(Collection<Setting> settings) {
		super.updateGuiParts(settings);
		toggler.enabled = enabled();
		toggler.updateText(getUnlocalizedName(), unlocalizedValue, value);
	}

	public static Builder<? extends SettingBoolean> builder(String name) {
		return new Builder<>(name, SettingBoolean::new);
	}

	public static class Builder<T extends SettingBoolean> extends SettingAlignable.Builder<T, Builder<T>> {
		private final Function<Builder<T>, T> buildFunc;

		protected Builder(String name, Function<Builder<T>, T> buildFunc) {
			super(name);
			this.buildFunc = buildFunc;
		}

		private String unlocalizedValue;
		public Builder<T> setValuePrefix(String value) {
			this.unlocalizedValue = value;
			return getThis();
		}

		@Override
		protected Builder<T> getThis() {
			return this;
		}

		@Override
		public T build() {
			return buildFunc.apply(this);
		}
	}
}
