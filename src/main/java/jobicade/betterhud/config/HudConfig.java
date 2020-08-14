package jobicade.betterhud.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingValueException;
import jobicade.betterhud.registry.HudElements;
import jobicade.betterhud.util.SortedSetList;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

/**
 * Handles saving and loading config files through Forge's system. Note that
 * actual settings are stored in each element's settings object.
 */
public class HudConfig {
    public static final HudConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        Pair<HudConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(HudConfig::new);
        CLIENT = specPair.getLeft();
        CLIENT_SPEC = specPair.getRight();
    }

    private final ConfigValue<List<? extends HudElement<?>>> enabledProperty;

    public HudConfig(ForgeConfigSpec.Builder builder) {
        builder.push("betterhud");
        enabledProperty = builder.defineList("enabled", Collections.<HudElement<?>>emptyList(), HudElements.get()::isRegistered);
        builder.pop();

        for (HudElement<?> element : HudElements.get().getRegistered()) {
            mapValues(builder, element.getRootSetting());
        }
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

        if (hasChanged()) {
            save();
        }
    }

    private void loadSelected() {
        available = new SortedSetList<>(new ArrayList<>(), Comparator.comparing(HudElement::getLocalizedName));
        selected = new ArrayList<>();

        available.addAll(HudElements.get().getRegistered());

        String[] enabledNames = getEnabledProperty().getStringList();
        for (String name : enabledNames) {
            HudElement<?> element = HudElements.get().getRegistered(name);

            if (element != null) {
                move(element, available, selected);
            }
        }
    }

    public void saveSettings() {
        // Convert current selected elements to string list for loading
        String[] enabledNames = new String[selected.size()];

        for (int i = 0; i < selected.size(); i++) {
            enabledNames[i] = selected.get(i).getName();
        }
        getEnabledProperty().set(enabledNames);

        for (Map.Entry<Setting, Property> entry : getPropertyMap().entrySet()) {
            entry.getValue().set(entry.getKey().getStringValue());
        }

        if (hasChanged()) {
            save();
        }
    }

    private Property getEnabledProperty() {
        return get(BetterHud.MODID, "enabledElements", new String[0]);
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

    private final Map<Setting, ConfigValue<String>> valueMap = new HashMap<>();

    public void loadValues() {
        for (Map.Entry<Setting, ConfigValue<String>> entry : valueMap.entrySet()) {
            try {
                entry.getKey().loadStringValue(entry.getValue().get());
            } catch (SettingValueException e) {
                String path = String.join(".", entry.getValue().getPath());
                BetterHud.getLogger().error("Parsing " + path + "=" + entry.getValue().get(), e);
            }
        }
    }

    private void mapValues(ForgeConfigSpec.Builder builder, Setting setting) {
        String name = setting.getName();
        boolean hasName = name != null && !name.isEmpty();

        if (hasName) {
            builder.push(name);

            if (setting.hasValue()) {
                valueMap.put(setting, builder.define(Collections.emptyList(), ""));
            }
        }

        for (Setting childSetting : setting.getChildren()) {
            mapValues(builder, childSetting);
        }

        if (hasName) {
            builder.pop();
        }
    }
}
