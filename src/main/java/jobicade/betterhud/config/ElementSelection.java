package jobicade.betterhud.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.gui.ElementCategory;

public class ElementSelection {
    private final Index<ElementCategory, HudElement<?>> available;
    private final Index<ElementCategory, HudElement<?>> selected;

    public ElementSelection(Iterable<? extends HudElement<?>> available) {
        this.available = Index.enumIndex(ElementCategory.class, available, TreeSet::new, HudElement::getCategory);
        this.selected = Index.enumIndex(ElementCategory.class, Collections.emptyList(), ArrayList::new, HudElement::getCategory);
    }

    public Index<ElementCategory, HudElement<?>> getAvailable() {
        return available;
    }

    public Index<ElementCategory, HudElement<?>> getSelected() {
        return selected;
    }

    public void select(HudElement<?> element) {
        if (available.remove(element)) {
            selected.add(element);
        }
    }

    public void deselect(HudElement<?> element) {
        if (selected.remove(element)) {
            available.add(element);
        }
    }

    public void clearSelection() {
        for (Collection<HudElement<?>> elements : selected.get)
    }
}
