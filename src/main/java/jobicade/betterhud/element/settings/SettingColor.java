package jobicade.betterhud.element.settings;

import jobicade.betterhud.render.Color;

public class SettingColor extends Setting {
	protected final SettingSlider red, green, blue;

	public SettingColor(Builder builder) {
		super(builder);

		addChildren(
			red = new SettingSlider("red", 0, 255, 1),
			green = new SettingSlider("green", 0, 255, 1),
			blue = new SettingSlider("blue", 0, 255, 1)
		);
	}

	public void set(Color color) {
		red.set(color.getRed());
		green.set(color.getGreen());
		blue.set(color.getBlue());
	}

	public Color get() {
		return new Color(red.get().intValue(), green.get().intValue(), blue.get().intValue());
	}

	public static Builder builder(String name) {
		return new Builder(name);
	}

	public static class Builder extends Setting.Builder<SettingColor, Builder> {
		protected Builder(String name) {
			super(name);
		}

		@Override
		protected Builder getThis() {
			return this;
		}

		@Override
		public SettingColor build() {
			return new SettingColor(this);
		}
	}
}
