package tk.nukeduck.hud.element.particles;

import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.RANDOM;

public class ParticleWater extends Particle {
	float opacity;
	float size;
	int u;
	float speed;

	public ParticleWater(int x, int y, float opacity, float size, int u, float speed) {
		super(x, y);
		this.opacity = opacity;
		this.size = size;
		this.u = u;
		this.speed = speed;
	}

	public static ParticleWater random(int width, int height) {
		int x = RANDOM.nextInt(width);
		int y = RANDOM.nextInt(height);
		float opacity = RANDOM.nextFloat() / 2;
		float size = 2f + RANDOM.nextFloat() * 4.5f;
		int u = RANDOM.nextInt(2);
		float speed = 100f + RANDOM.nextFloat() * 250f;
		return new ParticleWater(x, y, opacity, size, u, speed);
	}

	@Override
	public void render() {
		glPushMatrix(); {
			glColor4f(1.0F, 1.0F, 1.0F, this.opacity);
			glTranslatef(this.x, this.y - this.opacity * this.speed, 0.0F);
			glScalef(this.size, this.size, 1.0F);
			MC.ingameGUI.drawTexturedModalRect(0, 0, this.u * 16, 96, 16, 16);
		}
		glPopMatrix();
	}

	@Override
	public boolean update() {
		return (this.opacity -= 0.003) <= 0;
	}
}
