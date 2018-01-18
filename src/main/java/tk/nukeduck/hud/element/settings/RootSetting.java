package tk.nukeduck.hud.element.settings;

import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.util.HudConfig;

public class RootSetting extends SettingStub<Boolean> {
	private final HudElement element;
	private final SettingBoolean enabled = new SettingBoolean("enabled");

	public final void bindConfig(HudConfig config) {
		bindConfig(config, element.name, new StringBuilder());
	}

	public RootSetting(HudElement element) {
		super();
		this.element = element;

		add(enabled.setHidden());
	}

	@Override
	public Boolean get() {
		return enabled.get();
	}

	@Override
	public void set(Boolean value) {
		enabled.set(value);
	}

	public void toggle() {
		set(!get());
	}
}
