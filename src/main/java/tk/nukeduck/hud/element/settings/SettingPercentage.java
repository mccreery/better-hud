package tk.nukeduck.hud.element.settings;

public class SettingPercentage extends SettingSlider {
	public SettingPercentage(String name) {
		this(name, -1);
	}

	public SettingPercentage(String name, double accuracy) {
		super(name, 0, 100, accuracy);
		setUnlocalizedValue("betterHud.strings.percent");
	}

	@Override
	public String getDisplayValue(double value) {
		return super.getDisplayValue(value / 100);
	}
}
