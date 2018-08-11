package tk.nukeduck.hud.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.graph.Graph;

import net.minecraft.util.math.MathHelper;

public final class MathUtil {
	private MathUtil() {}

	/** @param dividend The left-hand argument of the division
	 * @param divisor The right-hand argument of the division
	 * @return The ceiling of the quotient ({@code dividend / divisor}) */
	public static int ceilDiv(int dividend, int divisor) {
		if(divisor < 0) {
			dividend = -dividend;
			divisor  = -divisor;
		}
		return (dividend + divisor - 1) / divisor;
	}

	/** @return The closest {@code y >= x} such that
	 * {@code y} is a multiple of {@code multiple} */
	public static int ceil(int x, int multiple) {
		return ceilDiv(x, multiple) * multiple;
	}

	/** @see Math#min(int, int) */
	public static int min(int... values) {
		int min = values[0];

		for(int i = 1; i < values.length; i++) {
			if(values[i] < min) min = values[i];
		}
		return min;
	}

	/** Partitions {@code items} such that all elements that satisfy {@code condition}
	 * are guaranteed to be placed further right than the rightmost element that does not.
	 * <p>This operation is not stable, i.e. the order of 
	 *
	 * @param items The list to partition. Is modified in place
	 * @param condition The condition for elements to be placed to the right
	 * @return The lowest index {@code i} such that {@code condition.test(i) == true} */
	public static <T> int partition(List<? extends T> items, Predicate<T> condition) {
		int i = 0;
		int j = items.size();

		while(i != j) {
			while(!condition.test(items.get(i))) {
				if(++i == j) return i;
			}
			do {
				if(i == --j) return i;
			} while(condition.test(items.get(j)));

			Collections.swap(items, i, j);
			++i;
		}
		return i;
	}

	/** Avoids autoboxing to {@link Integer}
	 * @see Objects#hash(Object...) */
	public static int hash(int... values) {
		int hashCode = 1;

		for(int x : values) {
			hashCode = 31 * hashCode + x;
		}
		return hashCode;
	}

	private static final Random RANDOM = new Random();

	/** {@code min} defaults to 0, {@code random} defaults to {@link #RANDOM}
	 * @see #randomRange(int, int, Random) */
	public static int randomRange(int max) {return randomRange(0, max, RANDOM);}

	/** {@code random} defaults to {@link #RANDOM}
	 * @see #randomRange(int, int, Random) */
	public static int randomRange(int min, int max) {return randomRange(min, max, RANDOM);}

	/** @param min The low end of the range (inclusive)
	 * @param max The high end of the range (exclusive)
	 * @param random The randomiser
	 * @return A random integer between the specified values */
	public static int randomRange(int min, int max, Random random) {
		return min + random.nextInt(max - min);
	}

	/** {@code min} defaults to 0, {@code random} defaults to {@link #RANDOM}
	 * @see #randomRange(float, float, Random) */
	public static float randomRange(float max) {return randomRange(0, max, RANDOM);}

	/** {@code random} defaults to {@link #RANDOM}
	 * @see #randomRange(float, float, Random) */
	public static float randomRange(float min, float max) {return randomRange(min, max, RANDOM);}

	/** @param min The low end of the range (inclusive)
	 * @param max The high end of the range (exclusive)
	 * @param random The randomiser
	 * @return A random float between the specified values */
	public static float randomRange(float min, float max, Random random) {
		return min + random.nextFloat() * (max - min);
	}

	/** {@code random} defaults to {@link #RANDOM}
	 * @see #randomChance(float, Random) */
	public static boolean randomChance(float probability) {
		return randomChance(probability, RANDOM);
	}

	/** @param probability The chance to return {@code true}.
	 * Passing {@code probability <= 0} will always return {@code false},
	 * and passing {@code probability >= 1} will always return {@code true}
	 * @param random The randomiser
	 * @return {@code true} with a {@code probability} chance */
	public static boolean randomChance(float probability, Random random) {
		return random.nextFloat() < probability;
	}

	public static <T> Collection<T> createRepeat(int n, Supplier<T> supplier) {
		return addRepeat(new ArrayList<>(n), n, supplier);
	}
	public static <T> Collection<T> createRepeat(int n, IntFunction<T> mapper) {
		return addRepeat(new ArrayList<>(n), n, mapper);
	}

	public static <T, C extends Collection<T>> C addRepeat(C collection, int n, Supplier<T> supplier) {
		return addRepeat(collection, n, i -> supplier.get());
	}
	public static <T, C extends Collection<T>> C addRepeat(C collection, int n, IntFunction<T> mapper) {
		return IntStream.range(0, n).mapToObj(mapper).collect(Collectors.toCollection(() -> collection));
	}

	/** Maps values from 0 to 1 to a specified range
	 * @param x The value to map
	 * @param min The value mapped to 0
	 * @param max The value mapped to 1 */
	public static float mapToRange(float x, float min, float max) {
		return min + (max - min) * x;
	}

	/** Sorts the nodes of {@code graph} such that for each edge {@code (n, m)},
	 * node {@code n} comes before node {@code m}
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/Topological_sorting">Topological sorting</a>
	 * @return A list containing a topological sort of {@code graph} */
	public static <T> List<T> topologicalSort(Graph<T> graph) {
		List<T> sortedNodes = new ArrayList<>(graph.nodes().size());
		Set<T> unmarked = new HashSet<>(graph.nodes());

		while(!unmarked.isEmpty()) {
			topologicalSortVisit(graph, unmarked, sortedNodes, unmarked.iterator().next());
		}
		// See below, after reversing predecessors come before successors
		Collections.reverse(sortedNodes);

		return sortedNodes;
	}

	private static <T> void topologicalSortVisit(Graph<T> graph, Set<T> unvisited, List<T> sortedNodes, T predecessor) {
		// If remove returns false, the node is already visited
		if(!unvisited.remove(predecessor)) return;

		for(T successor : graph.successors(predecessor)) {
			topologicalSortVisit(graph, unvisited, sortedNodes, successor);
		}
		// predecessor is now after all its successors (reverse topological order)
		sortedNodes.add(predecessor);
	}

	/** @return an {@code int} version of {@code health}
	 * for use in health bars */
	public static int getHealthForDisplay(float health) {
		return MathHelper.ceil(health);
	}
}
