package tk.nukeduck.hud.util;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Comparator;

public class Indexer<T> extends AbstractList<T> {
	private static final Integer[] EMPTY_INDEX = new Integer[0];

	private final List<T> data;
	private final Map<Comparator<T>, Integer[]> indices = new HashMap<Comparator<T>, Integer[]>();

	private Comparator<T> comparator;
	private Order order;

	private final ProxyComparator<T> proxyComparator;

	@SafeVarargs
	public Indexer(List<T> data, Comparator<T> comparator, Order order, Comparator<T>... index) {
		this(data, comparator, order, Arrays.asList(index));
	}

	public Indexer(List<T> data, Comparator<T> comparator, Order order, Collection<Comparator<T>> candidates) {
		this.data = data;
		proxyComparator = new ProxyComparator<T>(data, null);

		addComparators(candidates);
		setComparator(comparator, order);
	}

	public void changeComparator(Comparator<T> comparator) {
		if(this.comparator == comparator) {
			order = order.opposite();
		} else {
			setComparator(comparator, Order.ASCENDING);
			System.out.println("Comparator is now " + this.comparator);
		}
	}

	public void setComparator(Comparator<T> comparator, Order order) {
		addComparators(comparator);
		this.comparator = comparator;
		this.order = order;
	}

	public Comparator<T> getComparator() {
		return comparator;
	}

	public Order getOrder() {
		return order;
	}

	@SafeVarargs
	public final void addComparators(Comparator<T>... comparators) {
		addComparators(Arrays.asList(comparators));
	}

	public void addComparators(Collection<Comparator<T>> comparators) {
		for(Comparator<T> comparator : comparators) {
			if(!indices.containsKey(comparator)) {
				recalculateIndex(comparator);
			}
		}
	}

	public final Set<Comparator<T>> getComparators() {
		return indices.keySet();
	}

	public void recalculateIndices() {
		for(Comparator<T> comparator : indices.keySet()) {
			recalculateIndex(comparator);
		}
	}

	private void recalculateIndex(Comparator<T> comparator) {
		Integer[] index = indices.containsKey(comparator) ? indices.get(comparator) : EMPTY_INDEX;
		int previousSize = index.length;

		// Ensure that the index contains indices from 0 to index.length-1, in any order
		if(previousSize == 0 || previousSize > data.size()) {
			indices.put(comparator, index = new Integer[data.size()]);
			previousSize = 0;
		} else if(previousSize < data.size()) {
			indices.put(comparator, index = Arrays.copyOf(index, data.size()));
		}

		for(int i = previousSize; i < index.length; i++) {
			index[i] = i;
		}

		proxyComparator.setComparator(comparator);
		Arrays.sort(index, proxyComparator);
	}

	public void updateIndex(Comparator<T> comparator, int i) {
		if(indices.containsKey(comparator)) {
			Integer[] index = indices.get(comparator);
	
			proxyComparator.setComparator(comparator);
			SortUtil.updateSort(Arrays.asList(index), proxyComparator, ArrayUtils.indexOf(index, i));
		} else {
			recalculateIndex(comparator);
		}
	}

	@Override
	public T get(int i) {
		if(order == Order.DESCENDING) {
			i = data.size() - 1 - i;
		}
		return data.get(indices.get(comparator)[i]);
	}

	@Override
	public int size() {
		return data.size();
	}

	public enum Order {
		ASCENDING, DESCENDING;

		public Order opposite() {
			if(this == DESCENDING) {
				return ASCENDING;
			} else {
				return DESCENDING;
			}
		}
	}

	/** Compares indices based on their corresponding values */
	private static class ProxyComparator<T> implements Comparator<Integer> {
		List<T> data;
		Comparator<T> comparator;

		ProxyComparator(List<T> data, Comparator<T> comparator) {
			setData(data);
			setComparator(comparator);
		}

		void setData(List<T> data) {
			this.data = data;
		}

		void setComparator(Comparator<T> comparator) {
			this.comparator = comparator;
		}

		@Override
		public int compare(Integer a, Integer b) {
			return comparator.compare(data.get(a), data.get(b));
		}
	}
}
