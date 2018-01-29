package tk.nukeduck.hud.gui;

import tk.nukeduck.hud.element.settings.Setting;

public class GuiSettingToggle extends GuiToggleButton {
	private final Setting<Boolean> setting;

	public GuiSettingToggle(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, Setting<Boolean> setting) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		this.setting = setting;

		updateText();
	}

	public GuiSettingToggle(int buttonId, int x, int y, String buttonText, Setting<Boolean> setting) {
		super(buttonId, x, y, buttonText);
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
