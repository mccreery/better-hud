package jobicade.betterhud.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.graph.Graph;

import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import net.minecraft.util.math.MathHelper;

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
    private static Random currentRandom = RANDOM;

    /** Changes the random instance for random functions
     * @param random The random instance, or {@code null} to reset
     *
     * @see #randomRange(float, float)
     * @see #randomRange(int, int)
     * @see #randomPoint(Rect)
     * @see #randomChance(float, Random) */
    public static void setRandom(Random random) {
        currentRandom = random != null ? random : RANDOM;
    }

    /** @param min The low end of the range (inclusive)
     * @param max The high end of the range (exclusive)
     * @return A random integer between the specified values */
    public static int randomRange(int min, int max) {
        if(min < max) {
            return min + currentRandom.nextInt(max - min);
        } else if(min == max) {
            return min;
        } else {
            return max + currentRandom.nextInt(min - max);
        }
    }

    /** @param min The low end of the range (inclusive)
     * @param max The high end of the range (exclusive)
     * @return A random float between the specified values */
    public static float randomRange(float min, float max) {
        return min + currentRandom.nextFloat() * (max - min);
    }

    /** @param bounds The range
     * @return A random point within the bounds */
    public static Point randomPoint(Rect bounds) {
        return new Point(randomRange(bounds.getLeft(), bounds.getRight()),
            randomRange(bounds.getTop(), bounds.getBottom()));
    }

    /** @param probability The chance to return {@code true}. Clamped to [0,1]
     * @return {@code true} with a {@code probability} chance */
    public static boolean randomChance(float probability) {
        return currentRandom.nextFloat() < probability;
    }

    /** @return an {@code int} version of {@code health}
     * for use in health bars */
    public static int getHealthForDisplay(float health) {
        return MathHelper.ceil(health);
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
