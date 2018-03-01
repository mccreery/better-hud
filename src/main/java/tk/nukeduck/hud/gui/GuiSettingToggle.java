package tk.nukeduck.hud.gui;

import tk.nukeduck.hud.element.settings.Setting;

public class GuiSettingToggle extends GuiToggleButton {
	private final Setting<Boolean> setting;

	public GuiSettingToggle(String buttonText, Setting<Boolean> setting) {
		super(buttonText);
		this.setting = setting;

		updateText();
	}

	@Override
	public Boolean get() {
		if(setting != null) {
			return setting.get();
		} else {
			return false;
		}
	}

	@Override
	public void set(Boolean value) {
		if(setting != null) {
			setting.set(value);
		}
		updateText();
	}
}
