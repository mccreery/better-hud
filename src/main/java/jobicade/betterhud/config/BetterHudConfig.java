package jobicade.betterhud.config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.registry.HudRegistry;
import jobicade.betterhud.util.SortedSetList;

/**
 * Mod settings which exist outside of an individual element. Forge's config
 * system cannot be used as it does not support programmatic saving/loading or
 * generated specs (like the list of registered elements).
 */
public class BetterHudConfig {
    private List<HudElement<?>> disabled;
    private List<HudElement<?>> enabled;

    public BetterHudConfig(HudRegistry<?> elementRegistry, Data data) {
        Comparator<HudElement<?>> comparator = Comparator.comparing(HudElement::getLocalizedName);

        disabled = new SortedSetList<>(new ArrayList<>(), comparator);
        enabled = new ArrayList<>(data.enabled);

        disabled.addAll(elementRegistry.getRegistered());
    }

    public List<HudElement<?>> getDisabled() {
        return disabled;
    }

    public List<HudElement<?>> getEnabled() {
        return enabled;
    }

    public static class Data {
        private List<HudElement<?>> enabled;

        public Data(BetterHudConfig config) {
            enabled = config.enabled;
        }
    }
}
