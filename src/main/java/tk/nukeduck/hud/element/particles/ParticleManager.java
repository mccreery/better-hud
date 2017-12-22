package tk.nukeduck.hud.element.particles;

import java.util.ArrayList;
import java.util.Iterator;

public class ParticleManager<T extends Particle> {
	public final ArrayList<T> particles = new ArrayList<T>();
	
	public void update() {
		Iterator<T> it = this.particles.iterator();
		while(it.hasNext()) {
			if(it.next().update()) it.remove();
		}
	}
	
	public void renderAll() {
		for(Particle particle : this.particles) {
			particle.render();
		}
	}
}