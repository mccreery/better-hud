package jobicade.betterhud.element.settings;

import jobicade.betterhud.config.HudConfig;
import jobicade.betterhud.element.HudElement;

public class RootSetting extends SettingStub {
	private final HudElement<?> element;

	private final SettingBoolean enabled = new SettingBoolean("enabled").setHidden();
	// TODO nasty publicses
	public final SettingInteger priority = new SettingInteger("priority").setHidden();

	public final void bindConfig(HudConfig config) {
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
