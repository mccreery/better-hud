package jobicade.betterhud.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.HudElement.SortType;

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
