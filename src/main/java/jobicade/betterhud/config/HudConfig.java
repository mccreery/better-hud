package jobicade.betterhud.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingValueException;
import jobicade.betterhud.registry.HudElements;
import jobicade.betterhud.registry.SortField;
import jobicade.betterhud.util.SortedSetList;
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

	private SortedSetList<HudElement<?>> available;
	private List<HudElement<?>> selected;

	public List<HudElement<?>> getAvailable() {
		return available;
	}

	public List<HudElement<?>> getSelected() {
		return selected;
	}

	public void sortAvailable() {
		available.sort();
	}

	/**
	 * Moves an element from one collection to another if it is present in the
	 * source and not present in the destination.
	 *
	 * @return {@code true} if the element was transferred.
	 */
	public static <T> boolean move(T element, Collection<?> from, Collection<T> to) {
		if (from.contains(element) && !to.contains(element)) {
			from.remove(element);
			to.add(element);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Moves all elements from one collection to another.
	 */
	public static <T> void moveAll(Collection<? extends T> from, Collection<T> to) {
		to.addAll(from);
		from.clear();
	}

	@Override
	public void load() {
		super.load();
		loadSelected();

		for (Map.Entry<Setting, Property> entry : getPropertyMap().entrySet()) {
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

	private void loadSelected() {
		available = new SortedSetList<>(new ArrayList<>(), Comparator.comparing(HudElement::getLocalizedName));
		selected = new ArrayList<>();

		available.addAll(HudElements.get().getRegistered());

		String[] enabledNames = getStringList("enabledList", BetterHud.MODID, new String[0], "");
		for (String name : enabledNames) {
			HudElement<?> element = HudElements.get().getRegistered(name);

			if (element != null) {
				move(element, available, selected);
			}
		}
	}

	public void saveSettings() {
		for (Map.Entry<Setting, Property> entry : getPropertyMap().entrySet()) {
			entry.getValue().set(entry.getKey().getStringValue());
		}

		if (hasChanged()) {
			save();
		}
	}

	private Map<Setting, Property> getPropertyMap() {
		Map<Setting, Property> propertyMap = new HashMap<>();
		mapProperties(propertyMap, HudElements.GLOBAL.settings, HudElements.GLOBAL.getName(), "");

		for (HudElement<?> element : HudElements.get().getRegistered()) {
			mapProperties(propertyMap, element.settings, element.getName(), "");
		}
		return propertyMap;
	}

	private void mapProperties(Map<Setting, Property> map, Setting setting, String category, String pathPrefix) {
		String name = setting.getName();

		if (name != null && !name.isEmpty()) {
			if (pathPrefix.isEmpty()) {
				pathPrefix = name;
			} else {
				pathPrefix = pathPrefix + "." + name;
			}

			if (setting.hasValue()) {
				Property property = get(category, pathPrefix, setting.getStringValue());
				map.put(setting, property);
			}
		}

		for (Setting childSetting : setting.getChildren()) {
			mapProperties(map, childSetting, category, pathPrefix);
		}
	}
}
