package jobicade.betterhud.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class Sorter<T> {
	private final Iterable<T> sourceData;
	private final Map<Comparator<T>, List<T>> sortedDataMap = new HashMap<Comparator<T>, List<T>>();

	public Sorter(Iterable<T> sourceData) {
		this.sourceData = sourceData;
	}

	public final Collection<Comparator<T>> getComparators() {
		return sortedDataMap.keySet();
	}

	@SafeVarargs
	public final void markDirty(Comparator<T>... comparators) {
		if(comparators.length == 0) {
			sortedDataMap.clear();
		} else {
			for(Comparator<T> comparator : comparators) {
				sortedDataMap.remove(comparator);
			}
		}
	}

	public List<T> getSortedData(Comparator<T> comparator) {
		return getSortedData(comparator, false);
	}

	public List<T> getSortedData(Comparator<T> comparator, boolean descending) {
		List<T> sortedData = ensureEntry(comparator);
		if(descending) sortedData = Lists.reverse(sortedData);

		return Collections.unmodifiableList(sortedData);
	}

	private List<T> ensureEntry(Comparator<T> comparator) {
		return sortedDataMap.computeIfAbsent(comparator, c -> {
			List<T> sortedData = Lists.newArrayList(sourceData);
			sortedData.sort(c);

			return sortedData;
		});
	}
}
