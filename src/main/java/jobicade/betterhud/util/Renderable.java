package jobicade.betterhud.util;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;

public abstract class Renderable {
	public abstract Size getSize();
	protected abstract void renderUnsafe(Rect bounds, Direction contentAlignment);

	public boolean shouldRender() {return true;}

	public final void render(Rect bounds, Direction contentAlignment) {
		if(shouldRender()) {
			if(!bounds.getSize().equals(getSize())) {
				throw new IllegalArgumentException("Rect size does not equal size");
			}
			renderUnsafe(bounds, contentAlignment);
		}
	}
}
