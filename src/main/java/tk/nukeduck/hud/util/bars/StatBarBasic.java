package tk.nukeduck.hud.util.bars;

import java.util.ArrayList;
import java.util.List;

import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;

public abstract class StatBarBasic<T> extends StatBar<T> {
	protected abstract Bounds getIcon(IconType icon, int pointsIndex);
	protected abstract int getCurrent();

	@Override
	protected List<Bounds> getIcons(Direction alignment, int pointsIndex) {
		List<Bounds> textures = new ArrayList<>(2);
		textures.add(getIcon(IconType.BACKGROUND, pointsIndex));
		int current = getCurrent();

		if(pointsIndex < current) {
			textures.add(getIcon(pointsIndex + 1 < current ? IconType.FULL : IconType.HALF, pointsIndex));
		}
		return textures;
	}

	protected enum IconType {
		BACKGROUND, HALF, FULL;
	}
}
