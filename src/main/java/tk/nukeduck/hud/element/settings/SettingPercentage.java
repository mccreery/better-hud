package tk.nukeduck.hud.element.settings;

public class SettingPercentage extends SettingSlider {
	public SettingPercentage(String name) {
		this(name, 0, 1, -1);
	}

	public SettingPercentage(String name, double accuracy) {
		this(name, 0, 1, accuracy);
	}

	public SettingPercentage(String name, double min, double max) {
		this(name, min, max, -1);
	}

	public SettingPercentage(String name, double min, double max, double accuracy) {
		super(name, min, max, accuracy);
		setUnlocalizedValue("betterHud.strings.percent");
	}

	@Override
	public String getDisplayValue(double value) {
		return super.getDisplayValue(value * 100);
	}
}
