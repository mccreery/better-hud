package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.element.settings.Divider;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.util.Ticker;

public class FpsCount extends TextElement {
	private SettingBoolean numberOnly;
	
	@Override
	public void loadDefaults() {
		super.loadDefaults();
		numberOnly.value = false;
	}
	
	public FpsCount() {
		super("fpsCount");
		this.settings.add(0, new Divider("position"));
		this.settings.add(new Divider("misc"));
		this.settings.add(numberOnly = new SettingBoolean("numberOnly"));
		Ticker.FAST.register(this);
	}

	String currentFps = "";

	@Override
	public void update() {
		currentFps = MC.debug.split(" ")[0];
		if(!numberOnly.value) currentFps = I18n.format("betterHud.strings.fps", currentFps);
	}
	
	@Override
	public boolean shouldProfile() {
		return posMode.index == 1;
	}

	@Override
	protected String[] getText() {
		return new String[] {currentFps};
	}
}