package tk.nukeduck.hud.util;

import java.text.DecimalFormat;
import java.util.ArrayList;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;

public class FormatUtil {
	public static String formatTime(int hours, int minutes, boolean twentyFourHour) {
		String unlocalized, hourS, minuteS;

		if(twentyFourHour) {
			unlocalized = "betterHud.strings.time";
			hourS = String.format("%02d", hours);
		} else {
			final boolean pm = hours >= 12;
			if(pm) hours -= 12;
			if(hours == 0) hours = 12;

			unlocalized = pm ? "betterHud.strings.time.pm" : "betterHud.strings.time.am";
			hourS = String.valueOf(hours);
		}

		minuteS = String.format("%02d", minutes);
		return I18n.format(unlocalized, hourS, minuteS);
	}

	/** Formats {@code x} to {@code n} decimal places */
	public static String formatToPlaces(double x, int n) {
		DecimalFormat format = new DecimalFormat();
		format.setMaximumFractionDigits(n);

		return format.format(x);
	}

	public static int getLongestWidth(FontRenderer fr, ArrayList<String> strings) {
		int longest = 0;
		for(String s : strings) {
			int length = fr.getStringWidth(s);
			if(length > longest) longest = length;
		}
		return longest;
	}
	
	public static String separate(final String delimiter, String... parts) {
		if(parts.length == 0) {
			return "";
		} else if(parts.length == 1) {
			return parts[0];
		} else {
			StringBuilder builder = new StringBuilder(parts[0].length() * parts.length);
			builder.append(parts[0]);

			for(int i = 1; i < parts.length; i++) {
				builder.append(delimiter);
				builder.append(parts[i]);
			}
			return builder.toString();
		}
	}
}
