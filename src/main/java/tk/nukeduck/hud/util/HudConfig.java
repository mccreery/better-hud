package tk.nukeduck.hud.util;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import tk.nukeduck.hud.element.HudElement;

public class HudConfig extends Configuration {
	public HudConfig(File file) {
		super(file);
	}

	@Override
	public void load() {
		super.load();

		for(HudElement element : HudElement.ELEMENTS) {
			element.settings.bindConfig(this);
			element.settings.loadConfig();
		}
		if(hasChanged()) save();
	}

	public void saveSettings() {
		for(HudElement element : HudElement.ELEMENTS) {
			element.settings.saveConfig();
		}
		if(hasChanged()) save();
	}
}
