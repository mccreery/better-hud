package tk.nukeduck.hud.element.particles;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.RANDOM;

import net.minecraft.client.renderer.GlStateManager;
import tk.nukeduck.hud.util.Point;

public class ParticleWater extends Particle {
	float opacity;
	float size;
	int u;
	float speed;

	public ParticleWater(int x, int y, float opacity, float size, int u, float speed) {
		super(new Point(x, y));
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
		GlStateManager.pushMatrix();

		GlStateManager.color(1.0F, 1.0F, 1.0F, opacity);
		GlStateManager.translate(position.getX(), position.getY() - opacity * speed, 0.0F);
		GlStateManager.scale(size, size, 1.0F);
		MC.ingameGUI.drawTexturedModalRect(0, 0, u * 16, 96, 16, 16);

		GlStateManager.popMatrix();
	}

	@Override
	public boolean update() {
		return (this.opacity -= 0.003) <= 0;
	}
}
