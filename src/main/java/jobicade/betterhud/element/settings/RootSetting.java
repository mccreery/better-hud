package jobicade.betterhud.element.settings;

import jobicade.betterhud.config.HudConfig;
import jobicade.betterhud.element.HudElement;

public class RootSetting extends SettingStub<Boolean> {
	private final HudElement<?> element;

	private final SettingBoolean enabled = new SettingBoolean("enabled").setHidden();
	// TODO nasty publicses
	public final SettingInteger priority = new SettingInteger("priority").setHidden();

	public final void bindConfig(HudConfig config) {
		// TODO config file now using registry name
		// e.g. armorBars --> betterhud:armor_bars
		bindConfig(config, element.getName().toString(), "");
	}

	public RootSetting(HudElement<?> element) {
		super();
		this.element = element;

		addChild(enabled);
		addChild(priority);
	}

	public boolean isEnabled() {
		return enabled.get();
	}

	public void setEnabled(boolean enabled) {
		this.enabled.set(enabled);
	}

	@Override
	public boolean isEmpty() {
		return children.size() <= 2;
	}
}
