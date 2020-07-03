package jobicade.betterhud.config;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Mutable multimap where the key is a property of the value.
 */
public final class Index<K, V> {
    private final Map<K, Collection<V>> index;
    private final Function<K, Collection<V>> computeFunc;
    private final Function<V, K> keyFunc;

    private Index(Iterable<? extends V> available, Supplier<? extends Map<K, Collection<V>>> newMap,
            Supplier<? extends Collection<V>> newCollection, Function<V, K> keyFunc) {
        index = newMap.get();
        computeFunc = k -> newCollection.get();
        this.keyFunc = keyFunc;
    }

    public static <K extends Enum<K>, V> Index<K, V> enumIndex(Class<K> keyClass, Iterable<? extends V> available,
            Supplier<? extends Collection<V>> newCollection, Function<V, K> keyFunc) {
        return new Index<>(available, () -> new EnumMap<>(keyClass), newCollection, keyFunc);
    }

    public static <K, V> Index<K, V> hashIndex(Iterable<? extends V> available,
            Supplier<? extends Collection<V>> newCollection, Function<V, K> keyFunc) {
        return new Index<>(available, HashMap::new, newCollection, keyFunc);
    }

    public Collection<V> get(K key) {
        return index.get(key);
    }

    public boolean add(V value) {
        return index.computeIfAbsent(keyFunc.apply(value), computeFunc).add(value);
    }

    public boolean remove(V value) {
        K key = keyFunc.apply(value);
        Collection<V> collection = index.get(key);

        if (collection != null && collection.remove(value)) {
            if (collection.isEmpty()) {
                index.remove(key);
            }

            return true;
        } else {
            return false;
        }
    }

    public void clear() {
        index.clear();
    }
}
