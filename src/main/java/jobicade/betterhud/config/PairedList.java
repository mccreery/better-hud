package jobicade.betterhud.config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Maintains a list of enabled items from a larger sorted set.
 */
public final class PairedList<T> {
    private SortedSet<T> disabled;
    private final List<T> enabled;

    public PairedList(Iterable<? extends T> source) {
        this (source, null);
    }

    public PairedList(Iterable<? extends T> source, Comparator<? super T> comparator) {
        disabled = new TreeSet<>(comparator);
        source.forEach(disabled::add);

        enabled = new ArrayList<>();
    }

    /**
     * @return The disabled items, sorted by the comparator used during
     * construction or the natural order.
     */
    public SortedSet<T> getDisabled() {
        return disabled;
    }

    /**
     * @return The enabled items, in order of the calls to
     * {@link #enableElement(Object)}.
     */
    public List<T> getEnabled() {
        return enabled;
    }

    /**
     * Enables {@code item} unless it is already enabled.
     */
    public void enable(T item) {
        // Condition prevents double add
        if (disabled.remove(item)) {
            enabled.add(item);
        }
    }

    /**
     * Disables {@code item} unless it is already disabled.
     */
    public void disable(T item) {
        // Condition prevents double remove
        if (disabled.remove(item)) {
            enabled.add(item);
        }
    }
}
