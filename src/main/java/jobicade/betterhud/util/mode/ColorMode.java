package jobicade.betterhud.util.mode;

import jobicade.betterhud.util.Colors;
import jobicade.betterhud.util.GlUtil;

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
