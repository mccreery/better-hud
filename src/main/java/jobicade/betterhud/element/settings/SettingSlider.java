package jobicade.betterhud.element.settings;

import java.util.List;
import java.util.Map;

import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.gui.GuiSlider;
import jobicade.betterhud.util.ISlider;
import jobicade.betterhud.util.MathUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class SettingSlider extends SettingAlignable implements ISlider, IStringSetting {
	protected GuiSlider slider;

	private final double min;
	private final double max;
	private final double interval;
	private final double displayScale;
	private final int displayPlaces;
	private final String unlocalizedValue;

	private double value;

	private SettingSlider(Builder builder) {
		super(builder);
		min = builder.min;
		max = builder.max;
		interval = builder.interval;
		displayScale = builder.displayScale;
		displayPlaces = builder.displayPlaces;
		unlocalizedValue = builder.unlocalizedValue;

		value = min;
	}

	@Override
	public String getDisplayString() {
		return getLocalizedName() + ": " + getDisplayValue(get() * displayScale);
	}

	public String getDisplayValue(double scaledValue) {
		String displayValue = MathUtil.formatToPlaces(scaledValue, displayPlaces);

		if(unlocalizedValue != null) {
			displayValue = I18n.format(unlocalizedValue, displayValue);
		}
		return displayValue;
	}

	@Override
	public void getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Rect bounds) {
		slider = new GuiSlider(0, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), this);

		parts.add(slider);
		callbacks.put(slider, this);
	}

	@Override public void actionPerformed(GuiElementSettings gui, GuiButton button) {}
	@Override public Double get() {return value;}

	public int getInt() {
		return get().intValue();
	}

	@Override
	public void set(Double value) {
		this.value = normalize(value);
		if(slider != null) slider.updateDisplayString();
	}

	public void set(int value) {
		set((double)value);
	}

	@Override
	public IStringSetting getStringSetting() {
		return this;
	}

	@Override
	public String getStringValue() {
		return get().toString();
	}

	@Override
	public String getDefaultValue() {
		return String.valueOf(getMinimum());
	}

	@Override
	public void loadStringValue(String save) {
		set(Double.valueOf(save));

		if(slider != null) {
			slider.updateDisplayString();
		}
	}

	@Override
	public void loadDefaultValue() {
		value = getMinimum();
	}

	@Override public Double getMinimum() {return min;}
	@Override public Double getMaximum() {return max;}
	@Override public Double getInterval() {return interval;}

	public static final class Builder extends SettingAlignable.Builder<SettingSlider, Builder> {
		public Builder(String name) {
			super(name);
		}

		@Override
		protected Builder getThis() {
			return this;
		}

		@Override
		public SettingSlider build() {
			return new SettingSlider(this);
		}

		private double min = 0;
		private double max = 1;
		public Builder setRange(int min, int max) {
			this.min = min;
			this.max = max;
			return this;
		}

		private double interval = -1;
		public Builder setInterval(double interval) {
			this.interval = interval;
			setAutomaticDisplayPlaces();
			return this;
		}

		private double displayScale = 1;
		public Builder setDisplayScale(double displayScale) {
			this.displayScale = displayScale;
			setAutomaticDisplayPlaces();
			return this;
		}

		private void setAutomaticDisplayPlaces() {
			if (!customDisplayPlaces) {
				if (MathUtil.isIntegral(interval * displayScale)) {
					displayPlaces = 0;
				} else {
					displayPlaces = 1;
				}
			}
		}

		private int displayPlaces = 1;
		private boolean customDisplayPlaces;

		public Builder setDisplayPlaces(int displayPlaces) {
			this.displayPlaces = displayPlaces;
			this.customDisplayPlaces = true;
			return this;
		}

		private String unlocalizedValue;
		public Builder setUnlocalizedValue(String unlocalizedValue) {
			this.unlocalizedValue = unlocalizedValue;
			return this;
		}

		public Builder setDisplayPercent() {
			return setUnlocalizedValue("betterHud.value.percent")
				.setDisplayScale(100)
				.setDisplayPlaces(0);
		}
	}
}
