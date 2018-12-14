package jobicade.betterhud.element.settings;

import jobicade.betterhud.util.Colors;

public class SettingColor extends SettingStub<Integer> {
	protected final SettingSlider red, green, blue;

	public SettingColor(String name) {
		super(name);
		add(red = new SettingSlider("red", 0, 255, 1));
		add(green = new SettingSlider("green", 0, 255, 1));
		add(blue = new SettingSlider("blue", 0, 255, 1));
	}

	@Override
	public void set(Integer color) {
		red.set(Double.valueOf(Colors.red(color)));
		green.set(Double.valueOf(Colors.green(color)));
		blue.set(Double.valueOf(Colors.blue(color)));
	}

	@Override
	public Integer get() {
		return Colors.fromRGB(red.get().intValue(), green.get().intValue(), blue.get().intValue());
	}
}
