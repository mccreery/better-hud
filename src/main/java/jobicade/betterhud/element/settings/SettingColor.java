package jobicade.betterhud.element.settings;

import jobicade.betterhud.render.Color;

public class SettingColor extends SettingStub {
	protected final SettingSlider red, green, blue;

	public SettingColor(String name) {
		super(name);

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
}
