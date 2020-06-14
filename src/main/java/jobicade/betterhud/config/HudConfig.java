package jobicade.betterhud.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.registry.HudElements;
import jobicade.betterhud.registry.SortField;

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

		HudElements.GLOBAL.settings.bindConfig(this);
		HudElements.GLOBAL.settings.loadConfig();

		for(HudElement<?> element : HudElements.get().getRegistered()) {
			element.settings.bindConfig(this);
			element.settings.loadConfig();
		}
		HudElements.get().invalidateSorts(SortField.ENABLED);
		HudElement.normalizePriority();

		if(hasChanged()) save();
	}

	public void saveSettings() {
		HudElements.GLOBAL.settings.saveConfig();

		for(HudElement<?> element : HudElements.get().getRegistered()) {
			element.settings.saveConfig();
		}
		if(hasChanged()) save();
	}
}
