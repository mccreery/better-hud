package jobicade.betterhud.element;

import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingPercentage;
import jobicade.betterhud.element.settings.SettingSlider;
import jobicade.betterhud.geom.Rect;

public class GlobalSettings extends HudElement<Object> {
	private SettingPercentage billboardScale;
	private SettingSlider billboardDistance;
	private SettingBoolean hideOnDebug;
	private SettingBoolean debugMode;

	public GlobalSettings() {
		super("global");

		settings.addChildren(
			billboardScale = new SettingPercentage("billboardScale"),
			billboardDistance = new SettingSlider("rayDistance", 5, 200).setUnlocalizedValue("betterHud.hud.meters"),
			hideOnDebug = new SettingBoolean("hideOnDebug"),
			debugMode = new SettingBoolean("debugMode")
		);
	}

	public float getBillboardScale() {
		return billboardScale.get().floatValue();
	}

	public float getBillboardDistance() {
		return billboardDistance.get().floatValue();
	}

	public boolean hideOnDebug() {
		return hideOnDebug.get();
	}

	public boolean isDebugMode() {
		return debugMode.get();
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		billboardScale.set(0.5);
		billboardDistance.set(100.0);
		hideOnDebug.set(true);
	}

	@Override
	public Rect render(Object context) {
		return null;
	}
}
