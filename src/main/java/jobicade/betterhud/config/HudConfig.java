package jobicade.betterhud.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.HudElement.SortType;

/**
 * Handles saving and loading config files through Forge's system. Note that
 * actual settings are stored in each element's settings object.
 */
public class HudConfig extends Configuration {
	public HudConfig(File file) {
		super(file);
	}

	@Override
	public void load() {
		super.load();

		HudElement.GLOBAL.settings.bindConfig(this);
		HudElement.GLOBAL.settings.loadConfig();

		for(HudElement element : HudElement.ELEMENTS) {
			element.settings.bindConfig(this);
			element.settings.loadConfig();
		}
		HudElement.SORTER.markDirty(SortType.ENABLED);
		HudElement.normalizePriority();

		if(hasChanged()) save();
	}

	public void saveSettings() {
		HudElement.GLOBAL.settings.saveConfig();

		for(HudElement element : HudElement.ELEMENTS) {
			element.settings.saveConfig();
		}
		if(hasChanged()) save();
	}
}
