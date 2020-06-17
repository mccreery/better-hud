package jobicade.betterhud.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.IStringSetting;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingValueException;
import jobicade.betterhud.registry.HudElements;
import jobicade.betterhud.registry.SortField;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

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

		for (Map.Entry<IStringSetting, Property> entry : getPropertyMap().entrySet()) {
			try {
				entry.getKey().loadStringValue(entry.getValue().getString());
			} catch (SettingValueException e) {
				BetterHud.getLogger().error("Parsing " + entry.getValue().getName() + "=" + entry.getValue().getString(), e);
			}
		}

		HudElements.get().invalidateSorts(SortField.ENABLED);
		HudElement.normalizePriority();

		if (hasChanged()) {
			save();
		}
	}

	public void saveSettings() {
		for (Map.Entry<IStringSetting, Property> entry : getPropertyMap().entrySet()) {
			entry.getValue().set(entry.getKey().getStringValue());
		}

		if (hasChanged()) {
			save();
		}
	}

	private Map<IStringSetting, Property> getPropertyMap() {
		Map<IStringSetting, Property> propertyMap = new HashMap<>();

		for (HudElement<?> element : HudElements.get().getRegistered()) {
			mapProperties(propertyMap, element.settings, element.getName(), "");
		}
		return propertyMap;
	}

	private void mapProperties(Map<IStringSetting, Property> map, Setting setting, String category, String pathPrefix) {
		String name = setting.getName();

		if (name != null && !name.isEmpty()) {
			if (pathPrefix.isEmpty()) {
				pathPrefix = name;
			} else {
				pathPrefix = pathPrefix + "." + name;
			}

			IStringSetting stringSetting = setting.getStringSetting();
			if (stringSetting != null) {
				Property property = get(category, pathPrefix, stringSetting.getDefaultValue());
				map.put(stringSetting, property);
			}
		}

		for (Setting childSetting : setting.getChildren()) {
			mapProperties(map, childSetting, category, pathPrefix);
		}
	}
}
