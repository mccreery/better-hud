package tk.nukeduck.hud.element.settings;

public class SettingPercentage extends SettingSlider {
	public SettingPercentage(String name) {
		this(name, 0, 1);
	}

	public SettingPercentage(String name, double min, double max) {
		super(name, min, max, -1);

		setUnlocalizedValue("betterHud.value.percent");
		setDisplayScale(100);
		setDisplayPlaces(0);
	}
}
