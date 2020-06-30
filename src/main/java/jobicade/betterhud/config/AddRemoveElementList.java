package jobicade.betterhud.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.gui.ElementCategory;

/**
 * Maintains two lists of available and selected elements.
 */
public final class AddRemoveElementList {
    private final Map<ElementCategory, SortedSet<HudElement<?>>> available;
    private final Map<ElementCategory, List<HudElement<?>>> selected;

    public AddRemoveElementList(Iterable<HudElement<?>> registered) {
        available = new EnumMap<>(ElementCategory.class);
        selected = new EnumMap<>(ElementCategory.class);

        for (HudElement<?> element : registered) {
            available.putIfAbsent(element.getCategory(), new TreeSet<>()).add(element);
        }
    }

    /**
     * Selects an element if available.
     * @return {@code true} if the element could be selected.
     */
    public boolean add(HudElement<?> element) {
        return transfer(element, available, selected, ArrayList::new);
    }

    /**
     * Selects all available elements.
     */
    public void addAll() {
        List<HudElement<?>> availableList = new ArrayList<>();
        available.values().forEach(availableList::addAll);

        for (HudElement<?> element : availableList) {
            add(element);
        }
    }

    /**
     * Makes an element available if selected.
     * @return {@code true} if the element was previously selected.
     */
    public boolean remove(HudElement<?> element) {
        return transfer(element, selected, available, TreeSet::new);
    }

    /**
     * Makes all elements available.
     */
    public void removeAll() {
        List<HudElement<?>> selectedList = new ArrayList<>();
        selected.values().forEach(selectedList::addAll);

        for (HudElement<?> element : selectedList) {
            remove(element);
        }
    }

    private static <T extends Collection<HudElement<?>>> boolean transfer(HudElement<?> element,
            Map<ElementCategory, ? extends Collection<HudElement<?>>> source,
            Map<ElementCategory, T> dest, Supplier<T> computeDest) {
        ElementCategory category = element.getCategory();
        Collection<? super HudElement<?>> sourceColl = source.get(category);

        // Can only move to dest if moved from source
        if (sourceColl != null && sourceColl.remove(element)) {
            // Clean up unused categories
            if (sourceColl.isEmpty()) {
                source.remove(category);
            }

            dest.computeIfAbsent(category, c -> computeDest.get()).add(element);
            return true;
        } else {
            return false;
        }
    }
}
