package jobicade.betterhud.element.particles;

import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.Tickable;

import static jobicade.betterhud.BetterHud.MANAGER;

public interface Particle extends Tickable {
	public boolean isDead();
	public boolean shouldRender();
	public void render(float partialTicks);

	public static Rect getScreen() {
		if(MANAGER != null) {
			Rect bounds = MANAGER.getScreen();
			if(bounds != null) return bounds;
		}
		return Rect.empty();
	}
}
