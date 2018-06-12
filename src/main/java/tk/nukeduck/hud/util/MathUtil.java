package tk.nukeduck.hud.util;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

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
}
