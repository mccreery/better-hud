package jobicade.betterhud.element;

import java.util.List;

import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingPercentage;
import jobicade.betterhud.element.settings.SettingSlider;
import jobicade.betterhud.geom.Rect;

public class GlobalSettings extends HudElement {
	private SettingPercentage billboardScale;
	private SettingSlider billboardDistance;
	private SettingBoolean hideOnDebug;
	private SettingBoolean debugMode;

	public GlobalSettings() {
		super("global");
		ELEMENTS.remove(this);
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(billboardScale = new SettingPercentage("billboardScale"));
		settings.add(billboardDistance = new SettingSlider("rayDistance", 5, 200).setUnlocalizedValue("betterHud.hud.meters"));
		settings.add(hideOnDebug = new SettingBoolean("hideOnDebug"));
		settings.add(debugMode = new SettingBoolean("debugMode"));
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

	@Override public boolean shouldRender(Event event) {return false;}
	@Override public Rect render(Event event) {return null;}
}
