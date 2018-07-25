package tk.nukeduck.hud.element.particles;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.PARTICLES;

import net.minecraft.client.renderer.GlStateManager;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.MathUtil;
import tk.nukeduck.hud.util.Point;

public class ParticleWater extends ParticleBase {
	private float speed;

	protected ParticleWater(Point position, int iconIndex, float opacity, float size, float speed) {
		super(position, 96 + iconIndex, opacity, size, 0);
		this.speed = speed;
	}

	public static ParticleWater createRandom() {
		Point position = Point.createRandom(MANAGER.getScreen());

		float opacity = MathUtil.randomRange(0f, 0.5f);
		float size = MathUtil.randomRange(2f, 6.5f);
		float speed = MathUtil.randomRange(100f, 350f);

		int iconIndex = MathUtil.randomRange(2);
		return new ParticleWater(position, iconIndex, opacity, size, speed);
	}

	@Override
	public void render(float partialTicks) {
		MC.getTextureManager().bindTexture(PARTICLES);
		GlStateManager.pushMatrix();

		GlStateManager.translate(position.getX(), position.getY() - opacity * speed, 0);
		GlStateManager.scale(size, size, 1);
		GlStateManager.color(1, 1, 1, opacity);
		GlUtil.drawTexturedModalRect(Point.ZERO, texture);

		GlStateManager.popMatrix();
	}
}
