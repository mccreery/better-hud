package tk.nukeduck.hud.element.particles;

import tk.nukeduck.hud.util.Point;

public abstract class Particle {
	public Point position;

	public Particle(Point position) {
		this.position = position;
	}

	/** @return {@code true} if the particle is dead */
	public abstract boolean update();
	public abstract void render();
}
