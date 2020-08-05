package jobicade.betterhud.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestSortedSetList {
    List<Integer> backingList;
    SortedSetList<Integer> sortedSetList;
    Random random;

    @Before
    public void before() {
        backingList = new ArrayList<>();
        sortedSetList = new SortedSetList<>(backingList, Comparator.reverseOrder());
        // Set seed for consistent sequence
        random = new Random(42);
    }

    @Test
    public void testPassthru() {
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(i, sortedSetList.size());
            // Retry on duplicate
            while (!backingList.add(random.nextInt()));
        }
        Assert.assertEquals(backingList.size(), sortedSetList.size());

        for (int i = backingList.size() - 1; i >= 0; i--) {
            Assert.assertEquals(backingList.get(i), sortedSetList.get(i));
            sortedSetList.remove(i);
            Assert.assertEquals(i, sortedSetList.size());
        }
    }

    @Test
    public void testAddSort() {
        for (int i = 0; i < 10; i++) {
            sortedSetList.add(random.nextInt());
            Assert.assertTrue(isSorted(sortedSetList, Comparator.reverseOrder()));
        }
    }

    @Test
    public void testDoubleAdd() {
        Assert.assertTrue(sortedSetList.add(3));
        Assert.assertFalse(sortedSetList.add(3));
        Assert.assertEquals(1, sortedSetList.size());
    }

    @Test
    public void testManualSort() {
        for (int i = 0; i < 10; i++) {
            backingList.add(random.nextInt());
        }
        Assert.assertFalse(isSorted(sortedSetList, Comparator.reverseOrder()));
        sortedSetList.sort();
        Assert.assertTrue(isSorted(sortedSetList, Comparator.reverseOrder()));
    }

    @Test
    public void testTest() {
        // Sanity check for isSorted utility
        Assert.assertTrue(isSorted(Arrays.asList(1, 2, 4, 10, 15), Comparator.naturalOrder()));
        Assert.assertFalse(isSorted(Arrays.asList(1, 4, 15, 10, 2), Comparator.naturalOrder()));
        Assert.assertTrue(isSorted(Collections.emptyList(), Comparator.naturalOrder()));
    }

    private static <T> boolean isSorted(Iterable<T> iterable, Comparator<? super T> comparator) {
        Iterator<T> iterator = iterable.iterator();

        if (iterator.hasNext()) {
            T t1 = iterator.next();
            while (iterator.hasNext()) {
                T t2 = iterator.next();
                if (comparator.compare(t1, t2) > 0) {
                    return false;
                }
                t1 = t2;
            }
        }
        return true;
    }
}
