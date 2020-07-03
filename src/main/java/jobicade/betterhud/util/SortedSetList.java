package jobicade.betterhud.util;

import java.util.AbstractList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * List supporting add and remove akin to a sorted set. Used for index access.
 */
public class SortedSetList<T> extends AbstractList<T> {
    private final List<T> backingList;
    private final Comparator<? super T> comparator;

    public SortedSetList(List<T> backingList, Comparator<? super T> comparator) {
        this.backingList = backingList;
        this.comparator = comparator;
    }

    @Override
    public T get(int index) {
        return backingList.get(index);
    }

    @Override
    public int size() {
        return backingList.size();
    }

    @Override
    public boolean add(T e) {
        int index = Collections.binarySearch(backingList, e, comparator);

        if (index < 0) {
            backingList.add(-index - 1, e);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        return backingList.remove(o);
    }

    @Override
    public T remove(int index) {
        return backingList.remove(index);
    }

    public void sort() {
        backingList.sort(comparator);
    }
}
