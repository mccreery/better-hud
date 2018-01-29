package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;

import tk.nukeduck.hud.element.settings.SettingBoolean;

public class FpsCount extends TextElement {
	private final SettingBoolean numberOnly;

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		numberOnly.set(false);
	}

	public FpsCount() {
		super("fpsCount");
		this.settings.add(numberOnly = new SettingBoolean("numberOnly"));
	}

	@Override
	protected String[] getText() {
		String fps = MC.debug.substring(0, MC.debug.indexOf(' '));

		if(!numberOnly.get()) {
			fps = getLocalizedName() + ": " + fps;
		}
		return new String[] {fps};
	}
}
