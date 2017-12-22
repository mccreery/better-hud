package tk.nukeduck.hud.element.particles;

public abstract class Particle {
	public int x, y;
	
	public Particle(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/** @return {@code true} if the particle should be removed. */
	public abstract boolean update();
	public abstract void render();
}
