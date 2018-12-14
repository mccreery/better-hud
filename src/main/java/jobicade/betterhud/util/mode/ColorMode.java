package jobicade.betterhud.util.mode;

import jobicade.betterhud.util.render.Color;

public class ColorMode extends GlMode {
	private final Color color;

	public ColorMode(Color color) {
		this.color = color;
	}

	@Override
	public void begin() {
		color.apply();
	}

	@Override
	public void end() {
		Color.WHITE.apply();
	}
}
