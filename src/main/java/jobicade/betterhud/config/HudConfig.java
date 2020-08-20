package jobicade.betterhud.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final ConfigValue<List<? extends String>> enabledProperty;

    public HudConfig(ForgeConfigSpec.Builder builder) {
        builder.push("betterhud");
        enabledProperty = builder.defineList("enabled", Collections.<String>emptyList(), HudElements.get()::isRegistered);
        builder.pop();

        for (HudElement<?> element : HudElements.get().getRegistered()) {
            mapValues(builder, element.getRootSetting());
        }
    }

    private SortedSetList<HudElement<?>> available;
    private List<HudElement<?>> selected = new ArrayList<>();

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

    /**
     * Bakes values without reloading from file
     */
    public void load() {
        // TODO allow outside callers to reload from file e.g. after replacing
        loadEnabledList();
        loadElementSettings();
    }

    private void loadEnabledList() {
        available = new SortedSetList<>(new ArrayList<>(), Comparator.comparing(HudElement::getLocalizedName));
        selected = new ArrayList<>();

        available.addAll(HudElements.get().getRegistered());

        for (String name : enabledProperty.get()) {
            HudElement<?> element = HudElements.get().getRegistered(name);

            if (element != null) {
                move(element, available, selected);
            }
        }
    }

    private void loadElementSettings() {
        for (Map.Entry<Setting, ConfigValue<String>> entry : valueMap.entrySet()) {
            try {
                entry.getKey().loadStringValue(entry.getValue().get());
            } catch (SettingValueException e) {
                String path = String.join(".", entry.getValue().getPath());
                BetterHud.getLogger().error("Parsing " + path + "=" + entry.getValue().get(), e);
            }
        }
    }

    /**
     * Saves values to config value without writing the file.
     */
    public void save() {
        // TODO write file
        saveEnabledList();
        saveElementSettings();
    }

    private void saveEnabledList() {
        List<String> enabledNames = new ArrayList<>();
        for (HudElement<?> element : selected) {
            enabledNames.add(element.getName());
        }
        enabledProperty.set(enabledNames);
    }

    private void saveElementSettings() {
        for (Map.Entry<Setting, ConfigValue<String>> entry : valueMap.entrySet()) {
            entry.getValue().set(entry.getKey().getStringValue());
        }
    }

    private final Map<Setting, ConfigValue<String>> valueMap = new HashMap<>();

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
