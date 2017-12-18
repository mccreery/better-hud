package tk.nukeduck.hud.element.particles;

import net.minecraft.client.Minecraft;

public abstract class GuiParticle {
	public int x, y;
	
	public GuiParticle(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/** @return {@code true} if the particle should be removed. */
	public abstract boolean update(Minecraft mc);
	public abstract void render(Minecraft mc);
}
