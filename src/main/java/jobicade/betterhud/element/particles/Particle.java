package jobicade.betterhud.element.particles;

import jobicade.betterhud.util.Bounds;
import jobicade.betterhud.util.Tickable;

import static jobicade.betterhud.BetterHud.MANAGER;

public interface Particle extends Tickable {
	public boolean isDead();
	public boolean shouldRender();
	public void render(float partialTicks);

	public static Bounds getScreen() {
		if(MANAGER != null) {
			Bounds bounds = MANAGER.getScreen();
			if(bounds != null) return bounds;
		}
		return Bounds.EMPTY;
	}
}
