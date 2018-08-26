package tk.nukeduck.hud.util;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.HudElement.SortType;

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
		HudElement.SORTER.markDirty(SortType.ENABLED);
		HudElement.normalizePriority();

		if(hasChanged()) save();
	}

	public void saveSettings() {
		for(HudElement element : HudElement.ELEMENTS) {
			element.settings.saveConfig();
		}
		if(hasChanged()) save();
	}
}
