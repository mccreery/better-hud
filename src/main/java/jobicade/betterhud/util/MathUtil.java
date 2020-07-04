package jobicade.betterhud.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.graph.Graph;

public final class MathUtil {
    private MathUtil() {}

    /**
     * Performs linear interpolation.
     *
     * @param a The endpoint at {@code t == 0}.
     * @param b The endpoint at {@code t == 1}.
     * @param t Interpolation parameter. Can be outside {@code [0,1]}.
     */
    public static float lerp(float a, float b, float t) {
        // Higher precision than {a + (b - a) * t}
        return a * (1.0f - t) + b * t;
    }

    /**
     * Performs integer division rounding towards positive infinity.
     */
    public static int ceilDiv(int x, int y) {
        // Adapted from java.lang.Math.floorDiv
        int r = x / y;

        // if the signs are the same and modulo not zero, round up
        if ((x ^ y) >= 0 && (r * y != x)) {
            ++r;
        }
        return r;
    }

    /**
     * @return The least integer that is a multiple of {@code multiple} and
     * greater than or equal to {@code x}.
     */
    public static int ceil(int x, int multiple) {
        if (multiple == 0) {
            throw new IllegalArgumentException("multiple must not be 0");
        } else {
            multiple = Math.abs(multiple);
            return ceilDiv(x, multiple) * multiple;
        }
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

    /** Partitions {@code items} such that all elements that satisfy {@code condition}
     * are guaranteed to be placed further right than the rightmost element that does not.
     * <p>This operation is not stable
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

    /**
     * Splits a collection into two based on a predicate.
     * @param items The collection to split.
     * @param condition The condition predicate.
     * @param pass The collection to put passing values into.
     * @param fail The collection to put failing values into.
     */
    public static <T> void splitList(Iterable<? extends T> items, Predicate<T> condition, Collection<? super T> pass, Collection<? super T> fail) {
        for(T t : items) {
            if(condition.test(t)) {
                pass.add(t);
            } else {
                fail.add(t);
            }
        }
    }

    /**
     * Formats a number to a maximum number of decimal places.
     * @param x The number to format.
     * @param n The number of decimal places.
     * @return {@code x} formatted to a maximum of {@code n} decimal places.
     */
    public static String formatToPlaces(double x, int n) {
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(n);
        return format.format(x);
    }
}
