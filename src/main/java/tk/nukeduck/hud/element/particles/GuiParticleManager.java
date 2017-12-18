package tk.nukeduck.hud.element.particles;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.client.Minecraft;

public class GuiParticleManager<T extends GuiParticle> {
	public final ArrayList<T> particles = new ArrayList<T>();
	
	public void update(Minecraft mc) {
		Iterator<T> it = this.particles.iterator();
		while(it.hasNext()) {
			if(it.next().update(mc)) it.remove();
		}
	}
	
	public void renderAll(Minecraft mc) {
		for(GuiParticle particle : this.particles) {
			particle.render(mc);
		}
	}
}