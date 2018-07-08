package tk.nukeduck.hud.element;

import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.SettingPercentage;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;

public class GlobalSettings extends HudElement {
	private final SettingPercentage billboardScale = new SettingPercentage("billboardScale");
	private final SettingSlider billboardDistance = new SettingSlider("rayDistance", 5, 200).setUnlocalizedValue("betterHud.hud.meters");

	public GlobalSettings() {
		super("global");
		ELEMENTS.remove(this);

		settings.add(billboardScale);
		settings.add(billboardDistance);
	}

	public float getBillboardScale() {
		return billboardScale.get().floatValue();
	}

	public float getBillboardDistance() {
		return billboardDistance.get().floatValue();
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		billboardScale.set(0.5);
		billboardDistance.set(100.0);
	}

	@Override public boolean shouldRender(Event event) {return false;}
	@Override public Bounds render(Event event) {return null;}
}
