package jobicade.betterhud.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.registry.HudRegistry;

/**
 * Mod settings which exist outside of an individual element. Forge's config
 * system cannot be used as it does not support programmatic saving/loading or
 * generated specs (like the list of registered elements).
 */
public class BetterHudConfig {
    private static final Comparator<HudElement<?>> COMPARATOR = Comparator.comparing(HudElement::getLocalizedName);

    private List<HudElement<?>> disabled;
    private List<HudElement<?>> enabled;

    public BetterHudConfig(HudRegistry<?> elementRegistry) {
        disabled = new ArrayList<>(elementRegistry.getRegistered());
        enabled = new ArrayList<>();
        disabled.sort(COMPARATOR);
    }

    public BetterHudConfig(HudRegistry<?> elementRegistry, Data data) {
        this(elementRegistry);
        data.enabled.forEach(this::enable);
    }

    public List<HudElement<?>> getEnabled() {
        return enabled;
    }

    /**
     * Filters the list of enabled elements by those registered in one registry.
     */
    public <T extends HudElement<?>> List<T> getEnabled(HudRegistry<? extends T> registry) {
        List<T> enabled = new ArrayList<>();

        for (HudElement<?> element : getEnabled()) {
            T subclass = registry.getRegistered(element.getName());

            if (subclass != null) {
                enabled.add(subclass);
            }
        }
        return enabled;
    }

    public void enable(HudElement<?> element) {
        if (disabled.remove(element)) {
            enabled.add(element);
        }
    }

    public void enableAll() {
        enabled.addAll(disabled);
        disabled.clear();
    }

    public List<HudElement<?>> getDisabled() {
        return disabled;
    }

    public void disable(HudElement<?> element) {
        if (enabled.remove(element)) {
            int insertIndex = Collections.binarySearch(disabled, element, COMPARATOR);

            if (insertIndex < 0) {
                disabled.add(-insertIndex - 1, element);
            }
        }
    }

    public void disableAll() {
        disabled.addAll(enabled);
        disabled.sort(COMPARATOR);
        enabled.clear();
    }

    public static class Data {
        public List<HudElement<?>> enabled;

        public Data() {
        }

        public Data(BetterHudConfig config) {
            enabled = config.enabled;
        }
    }
}
