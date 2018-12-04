package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.Arrays;
import java.util.List;

import tk.nukeduck.hud.element.settings.Setting;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.util.Direction;

public class FpsCount extends TextElement {
	private SettingBoolean numberOnly;

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.setPreset(Direction.SOUTH_WEST);
		numberOnly.set(false);
	}

	public FpsCount() {
		super("fpsCount");
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(numberOnly = new SettingBoolean("numberOnly"));
	}

	@Override
	protected List<String> getText() {
		String fps = MC.debug.substring(0, MC.debug.indexOf(' '));

		if(!numberOnly.get()) {
			fps = getLocalizedName() + ": " + fps;
		}
		return Arrays.asList(fps);
	}
}
