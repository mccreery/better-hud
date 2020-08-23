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
    private List<HudElement<?>> available;
    private List<HudElement<?>> selected;

    public BetterHudConfig(HudRegistry<?> elementRegistry, Data data) {
        Comparator<HudElement<?>> comparator = Comparator.comparing(HudElement::getLocalizedName);

        available = new SortedSetList<>(new ArrayList<>(), comparator);
        selected = new ArrayList<>(data.selected);

        available.addAll(elementRegistry.getRegistered());
    }

    public static class Data {
        private List<HudElement<?>> selected;

        public Data(BetterHudConfig config) {
            selected = config.selected;
        }
    }
}
