package tk.nukeduck.hud.element.particles;

import tk.nukeduck.hud.util.Renderable;
import tk.nukeduck.hud.util.Tickable;

public interface Particle extends Tickable, Renderable {
	public boolean isDead();
}
