package tk.nukeduck.hud.element.particles;

import tk.nukeduck.hud.util.Tickable;

public interface Particle extends Tickable {
	public boolean isDead();
	public boolean shouldRender();
	public void render(float partialTicks);
}
