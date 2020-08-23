package jobicade.betterhud.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;

public abstract class HudRegistry<T extends HudElement<?>> {
    private final HudRegistry<? super T> parentRegistry;
    private final Map<String, T> elements = new HashMap<>();

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
        }
    }

    public final Collection<T> getRegistered() {
        return elements.values();
    }

    public final T getRegistered(Object name) {
        return elements.get(name);
    }

    public final boolean isRegistered(Object name) {
        return elements.containsKey(name);
    }
}
