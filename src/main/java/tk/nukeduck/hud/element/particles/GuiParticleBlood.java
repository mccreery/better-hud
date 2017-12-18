package tk.nukeduck.hud.element.particles;

import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.ArrayList;

import tk.nukeduck.hud.BetterHud;
import net.minecraft.client.Minecraft;

public class GuiParticleBlood extends GuiParticle {
	float opacity;
	float size;
	int rotation;
	int u, v;
	
	public GuiParticleBlood(int x, int y, float opacity, int rotation, float size, int u, int v) {
		super(x, y);
		this.opacity = opacity;
		this.size = size;
		this.rotation = rotation;
		this.u = u;
		this.v = v;
	}
	
	public static GuiParticleBlood random(int width, int height) {
		int x = BetterHud.random.nextInt(width);
		int y = BetterHud.random.nextInt(height);
		float opacity = BetterHud.random.nextFloat() / 2;
		int rotation = BetterHud.random.nextInt(360);
		float size = 2f + BetterHud.random.nextFloat() * 4f;
		int u = BetterHud.random.nextInt(4);
		int v = BetterHud.random.nextInt(4);
		return new GuiParticleBlood(x, y, opacity, rotation, size, u, v);
	}
	
	@Override
	public void render(Minecraft mc) {
		glPushMatrix(); {
			glColor4f(1.0F, 1.0F, 1.0F, this.opacity);
			glTranslatef(this.x, this.y, 0.0F);
			glRotatef(this.rotation, 0.0F, 0.0F, 1.0F);
			glScalef(this.size, this.size, 1.0F);
			mc.ingameGUI.drawTexturedModalRect(0, 0, this.u * 16, this.v * 16, 16, 16);
		}
		glPopMatrix();
	}

	@Override
	public boolean update(Minecraft mc) {
		return (this.opacity -= 0.003) <= 0;
	}
}