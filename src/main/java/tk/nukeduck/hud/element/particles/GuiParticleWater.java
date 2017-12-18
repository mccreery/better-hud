package tk.nukeduck.hud.element.particles;

import tk.nukeduck.hud.BetterHud;
import net.minecraft.client.Minecraft;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glScalef;

public class GuiParticleWater extends GuiParticle {
	float opacity;
	float size;
	int u;
	float speed;

	public GuiParticleWater(int x, int y, float opacity, float size, int u, float speed) {
		super(x, y);
		this.opacity = opacity;
		this.size = size;
		this.u = u;
		this.speed = speed;
	}
	
	public static GuiParticleWater random(int width, int height) {
		int x = BetterHud.random.nextInt(width);
		int y = BetterHud.random.nextInt(height);
		float opacity = BetterHud.random.nextFloat() / 2;
		float size = 2f + BetterHud.random.nextFloat() * 4.5f;
		int u = BetterHud.random.nextInt(2);
		float speed = 100f + BetterHud.random.nextFloat() * 250f;
		return new GuiParticleWater(x, y, opacity, size, u, speed);
	}
	
	@Override
	public void render(Minecraft mc) {
		glPushMatrix(); {
			glColor4f(1.0F, 1.0F, 1.0F, this.opacity);
			glTranslatef(this.x, this.y - this.opacity * this.speed, 0.0F);
			glScalef(this.size, this.size, 1.0F);
			mc.ingameGUI.drawTexturedModalRect(0, 0, this.u * 16, 96, 16, 16);
		}
		glPopMatrix();
	}

	@Override
	public boolean update(Minecraft mc) {
		return (this.opacity -= 0.003) <= 0;
	}
}