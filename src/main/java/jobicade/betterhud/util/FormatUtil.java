package jobicade.betterhud.util;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

/** Functions that are used in multiple parts of the mod */
public class FormatUtil {
	/** Formats {@code x} to {@code n} decimal places */
	public static String formatToPlaces(double x, int n) {
		DecimalFormat format = new DecimalFormat();
		format.setMaximumFractionDigits(n);

		return format.format(x);
	}

	/** @see #join(String, List, StringBuilder) */
	public static void join(String delimiter, StringBuilder builder, String... parts) {
		join(delimiter, Arrays.asList(parts), builder);
	}

	/** @see #join(String, List) */
	public static String join(String delimiter, String... parts) {
		return join(delimiter, Arrays.asList(parts));
	}

	/** @return The result of {@link #join(String, List, StringBuilder)} with an empty builder */
	public static String join(String delimiter, List<String> parts) {
		StringBuilder builder = new StringBuilder();

		join(delimiter, parts, builder);
		return builder.toString();
	}

	/** Appends {@code builder} with {@code parts} joined into a single string using {@code delimiter} */
	public static void join(String delimiter, List<String> parts, StringBuilder builder) {
		if(!parts.isEmpty()) {
			builder.ensureCapacity(builder.length() + parts.get(0).length() * parts.size());

			// Add parts
			for(String part : parts) {
				builder.append(part).append(delimiter);
			}
			builder.setLength(builder.length() - delimiter.length()); // Cut tail
		}
	}

	/** @return The result of {@link #repeat(String, String, int, StringBuilder)} with an empty builder */
	public static String repeat(String value, String delimiter, int times) {
		StringBuilder builder = new StringBuilder();

		repeat(value, delimiter, times, builder);
		return builder.toString();
	}

	/** Appends {@code builder} with {@code value} repeated {@code times} using {@code delimiter} */
	public static void repeat(String value, String delimiter, int times, StringBuilder builder) {
		if(times > 0) {
			builder.ensureCapacity(builder.length() + (value.length() + delimiter.length()) * times - delimiter.length());

			for(int i = 0; i < times; i++) {
				builder.append(value).append(delimiter);
			}
			builder.setLength(builder.length() - delimiter.length());
		}
	}
}
