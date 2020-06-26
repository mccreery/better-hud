package jobicade.betterhud.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.util.Sorter;

public abstract class HudRegistry<T extends HudElement<?>> {
    private final HudRegistry<? super T> parentRegistry;
    private final Map<String, T> elements = new HashMap<>();
    private final Sorter<T> sorter = new Sorter<>(elements.values());

    public HudRegistry(HudRegistry<? super T> parentRegistry) {
        this.parentRegistry = parentRegistry;
    }

    public final void register(Iterable<T> elements) {
        for (T element : elements) {
            register(element);
        }
    }

    @SafeVarargs
    public final void register(T... elements) {
        for (T element : elements) {
            register(element);
        }
    }

    public final void register(T element) {
        if (parentRegistry != null) {
            parentRegistry.register(element);
        }

        T oldElement = elements.put(element.getName(), element);
        if (oldElement != null) {
            BetterHud.getLogger().warn("Duplicate registry key \"%s\" overwritten");
            disabledElements.remove(oldElement);
        }
        disabledElements.add(element);

        sorter.markDirty();
    }

    public final List<T> getRegistered() {
        return getRegistered(SortField.ALPHABETICAL);
    }

    public final List<T> getRegistered(Comparator<? super T> comparator) {
        return sorter.getSortedData(comparator);
    }

    public final T getRegistered(String name) {
        return elements.get(name);
    }

    @SafeVarargs
    public final void invalidateSorts(Comparator<?>... comparators) {
        sorter.markDirty(comparators);

        if (parentRegistry != null) {
            parentRegistry.invalidateSorts(comparators);
        }
    }

    private final List<T> disabledElements = new ArrayList<>();
    private final List<T> enabledElements = new ArrayList<>();

    public final void enableElement(T element) {
        // Returns true if element was in disabled
        if (disabledElements.remove(element)) {
            // Lists are mutually exclusive so element is not in enabled
            enabledElements.add(element);
        }
    }

    public final void disableElement(T element) {
        // Returns true if element was in enabled
        if (enabledElements.remove(element)) {
            // Lists are mutually exclusive so element is not in disabled
            int index = Collections.binarySearch(disabledElements, element,
                Comparator.comparing(HudElement::getLocalizedName));

            // See return of binarySearch when value is not in the list
            disabledElements.add(-index - 1, element);
        }
    }

    public final void sortDisabled() {
        disabledElements.sort(Comparator.comparing(HudElement::getLocalizedName));
    }
}
