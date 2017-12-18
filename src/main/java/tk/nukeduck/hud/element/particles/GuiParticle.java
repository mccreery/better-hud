package tk.nukeduck.hud.element.particles;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;

public abstract class GuiParticle {
	public int x, y;
	
	public GuiParticle(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	//public static final ArrayList<GuiParticle> particles = new ArrayList<GuiParticle>();
	/*public static void renderAll(Minecraft mc) {
		for(GuiParticle particle : particles) {
			particle.render(mc);
		}
	}*/
	
	/** @return {@code true} if the particle should be removed. */
	public abstract boolean update(Minecraft mc);
	public abstract void render(Minecraft mc);
}