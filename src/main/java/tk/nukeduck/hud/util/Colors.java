package tk.nukeduck.hud.util;

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

	public static int fromRGB(int r, int g, int b) {
		return fromARGB(255, r, g, b);
	}
	public static int fromARGB(int a, int r, int g, int b) {
		return (a & 0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
	}

	public static int setAlpha(int color, int alpha) {
		return color | (alpha & 0xff) << 24;
	}

	public static int alpha(int color) {
		return (color >> 24) & 0xff;
	}
	public static int red(int color) {
		return (color >> 16) & 0xff;
	}
	public static int green(int color) {
		return (color >> 8) & 0xff;
	}
	public static int blue(int color) {
		return color & 0xff;
	}
}
