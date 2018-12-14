package jobicade.betterhud.util;

import net.minecraft.util.math.MathHelper;

/** Functions for working with 32-bit ARGB colors */
public class Colors {
	public static final int WHITE = fromRGB(0xff, 0xff, 0xff);
	public static final int GRAY  = fromRGB(0x3f, 0x3f, 0x3f);
	public static final int BLACK = fromRGB(0x00, 0x00, 0x00);

	public static final int RED   = fromRGB(0xff, 0x00, 0x00);
	public static final int GREEN = fromRGB(0x00, 0xff, 0x00);
	public static final int BLUE  = fromRGB(0x00, 0x00, 0xff);

	public static final int TRANSLUCENT = fromARGB(0x5f, 0x00, 0x00, 0x00);
	public static final int FOREGROUND  = fromARGB(0x7f, 0x3f, 0x3f, 0x3f);
	public static final int HIGHLIGHT   = fromARGB(0xbf, 0x3f, 0x3f, 0x3f);

	/** @return A 32-bit composite ARGB color converted from HSV */
	public static int fromHSV(float hue, float saturation, float value) {
		hue %= 1;
		if(hue < 0) ++hue; // Math.floorMod doesn't work on floats

		saturation = MathHelper.clamp(saturation, 0, 1);
		value = MathHelper.clamp(value, 0, 1);

		return setAlpha(MathHelper.hsvToRGB(hue, saturation, value), 0xff);
	}

	/** {@code alpha} defaults to {@code 0xff}
	 * @see #fromARGB(int, int, int, int) */
	public static int fromRGB(int red, int green, int blue) {
		return fromARGB(0xff, red, green, blue);
	}

	/** @return A 32-bit composite ARGB color */
	public static int fromARGB(int alpha, int red, int green, int blue) {
		return setAlpha(MathHelper.rgb(red & 0xff, green & 0xff, blue & 0xff), alpha);
	}

	/** @return {@code color} with its alpha component set to {@code alpha} */
	public static int setAlpha(int color, int alpha) {
		return (alpha & 0xff) << 24 | (color & 0xffffff);
	}

	/** @return The alpha component of {@code color} */
	public static int alpha(int color) {
		return (color >> 24) & 0xff;
	}

	/** @return The red component of {@code color} */
	public static int red(int color) {
		return (color >> 16) & 0xff;
	}

	/** @return The green component of {@code color} */
	public static int green(int color) {
		return (color >> 8) & 0xff;
	}

	/** @return The blue component of {@code color} */
	public static int blue(int color) {
		return color & 0xff;
	}
}
