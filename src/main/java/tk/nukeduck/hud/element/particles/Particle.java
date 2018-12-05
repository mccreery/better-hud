package tk.nukeduck.hud.element.particles;

import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Tickable;

import static tk.nukeduck.hud.BetterHud.MANAGER;

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
