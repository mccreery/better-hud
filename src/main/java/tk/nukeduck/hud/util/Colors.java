package tk.nukeduck.hud.util;

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

	/** @return a 32-bit ARGB representation of the given opaque RGB color */
	public static int fromRGB(int r, int g, int b) {
		return fromARGB(255, r, g, b);
	}

	/** @return A 32-bit ARGB representation of the given color */
	public static int fromARGB(int a, int r, int g, int b) {
		return (a & 0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
	}

	/** @return {@code color} with the alpha component set to {@code alpha} */
	public static int setAlpha(int color, int alpha) {
		return color | (alpha & 0xff) << 24;
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
