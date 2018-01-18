package tk.nukeduck.hud.gui;

import tk.nukeduck.hud.element.settings.Setting;

public class GuiSettingToggle extends GuiToggleButton {
	private final Setting<Boolean> setting;

	public GuiSettingToggle(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, boolean updateText, Setting<Boolean> setting) {
		super(buttonId, x, y, widthIn, heightIn, buttonText, false);
		this.setting = setting;

		this.updateText = updateText;
		updateText();
	}

	public GuiSettingToggle(int buttonId, int x, int y, String buttonText, boolean updateText, Setting<Boolean> setting) {
		super(buttonId, x, y, buttonText, false);
		this.setting = setting;

		this.updateText = updateText;
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
