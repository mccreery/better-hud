package tk.nukeduck.hud.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class StringManager {
	private final Map<Direction, List<ColoredText>> text = new HashMap<Direction, List<ColoredText>>();

	public StringManager() {
		text.put(Direction.NORTH_EAST, new ArrayList<ColoredText>());
		text.put(Direction.SOUTH_EAST, new ArrayList<ColoredText>());
		text.put(Direction.SOUTH_WEST, new ArrayList<ColoredText>());
		text.put(Direction.NORTH_WEST, new ArrayList<ColoredText>());
	}

	public void add(Direction corner, int color, String... text) {
		for(String line : text) {
			add(corner, new ColoredText(line, color));
		}
	}

	public void add(Direction corner, ColoredText value) {
		get(corner).add(value);
	}

	public List<ColoredText> get(Direction corner) {
		return text.get(corner);
	}
}
