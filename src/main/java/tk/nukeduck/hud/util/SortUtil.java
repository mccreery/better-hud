package tk.nukeduck.hud.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class SortUtil {
	/** {@code comparator} defaults to {@code null}
	 * @see #updateSort(List, Comparator, int) */
	public static <T> void updateSort(List<T> sortedList, int dirtyIndex) {
		updateSort(sortedList, null, dirtyIndex);
	}

	/** Moves a single updated value in a previously sorted list to its correct sorted position
	 *
	 * @param sortedList The (almost) sorted list
	 * @param comparator The comparator used to sort the list
	 * @param dirtyIndex The index of the updated element in {@code sortedList} */
	public static <T> void updateSort(List<T> sortedList, Comparator<? super T> comparator, int dirtyIndex) {
		T key = sortedList.get(dirtyIndex);

		int index = getInsertionPoint(sortedList.subList(0, dirtyIndex), key, comparator);

		if(index == dirtyIndex) {
			index = dirtyIndex + 1 + getInsertionPoint(sortedList.subList(dirtyIndex + 1, sortedList.size()), key, comparator);
		}
		moveBefore(sortedList, dirtyIndex, index);
	}

	/** {@code comparator} defaults to {@code null}
	 * @see #getInsertionPoint(List, Object, Comparator) */
	public static <T> int getInsertionPoint(List<T> sortedList, T key) {
		return getInsertionPoint(sortedList, key, null);
	}

	/** Finds a valid {@code index} for {@link List#add(int, Object)} on a sorted list such that the order is maintained
	 *
	 * @param sortedList A list of elements sorted by {@code comparator}
	 * @param key The new element to be inserted into {@code list}
	 * @param comparator The comparator used to sort the list<br>
	 * Note that passing {@code null} will use the natural order of {@code list}
	 * @return An index {@code i} such that {@code list.get(i-1) <= key && list.get(i) >= key}
	 *
	 * @see Collections#binarySearch(List, Object, Comparator) */
	public static <T> int getInsertionPoint(List<T> sortedList, T key, Comparator<? super T> comparator) {
		int i = Collections.binarySearch(sortedList, key, comparator);

		if(i < 0) {
			// sortedList does not contain key, i == -insertionPoint - 1
			i = -(i + 1);
		}
		return i;
	}

	/** Moves the element at {@code fromIndex} to before the element at {@code toIndex}.
	 *
	 * <p>After this operation, the element will be moved between the original elements
	 * {@code list.get(toIndex-1)} and {@code list.get(toIndex)} */
	public static <T> void moveBefore(List<T> list, int fromIndex, int toIndex) {
		if(toIndex > fromIndex) {
			// Move right
			Collections.rotate(list.subList(fromIndex, toIndex), -1);
		} else if(toIndex < fromIndex) {
			// Move left
			Collections.rotate(list.subList(toIndex, fromIndex + 1), 1);
		}
	}
}
