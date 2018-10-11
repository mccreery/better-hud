package tk.nukeduck.hud.util.mode;

import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.GlUtil;

public class ColorMode extends GlMode {
	private final int color;

	public ColorMode(int color) {
		this.color = color;
	}

	@Override
	public void begin() {
        GlUtil.color(color);
	}

	@Override
	public void end() {
		GlUtil.color(Colors.WHITE);
	}
}
