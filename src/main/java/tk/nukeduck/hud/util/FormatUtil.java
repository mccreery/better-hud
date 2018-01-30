package tk.nukeduck.hud.util;

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

	/** @see #join(String, List) */
	public static String join(String delimiter, String... parts) {
		return join(delimiter, Arrays.asList(parts));
	}

	/** @return {@code parts} joined into a single string using {@code delimiter} */
	public static String join(String delimiter, List<String> parts) {
		if(parts.isEmpty()) return "";

		// Estimate size of string builder
		StringBuilder builder = new StringBuilder(parts.get(0).length() * parts.size());

		// Add parts
		for(String part : parts) {
			builder.append(part).append(delimiter);
		}
		builder.setLength(builder.length() - delimiter.length()); // Cut tail
		return builder.toString();
	}
}
