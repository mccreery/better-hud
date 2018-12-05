package tk.nukeduck.hud.element.particles;

import static tk.nukeduck.hud.BetterHud.PARTICLES;

import net.minecraft.client.renderer.GlStateManager;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.MathUtil;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.mode.GlMode;
import tk.nukeduck.hud.util.mode.TextureMode;

public class ParticleWater extends ParticleBase {
	private float speed;

	protected ParticleWater(Point position, int iconIndex, float opacity, float size, float speed) {
		super(position, 96 + iconIndex, opacity, size, 0);
		this.speed = speed;
	}

	public static ParticleWater createRandom() {
		Point position = MathUtil.randomPoint(Particle.getScreen());

		float opacity = MathUtil.randomRange(0f, 0.5f);
		float size = MathUtil.randomRange(2f, 6.5f);
		float speed = MathUtil.randomRange(100f, 350f);

		int iconIndex = MathUtil.randomRange(0, 2);
		return new ParticleWater(position, iconIndex, opacity, size, speed);
	}

	@Override
	public void render(float partialTicks) {
		GlMode.push(new TextureMode(PARTICLES));
		GlStateManager.pushMatrix();

		GlStateManager.translate(position.getX(), position.getY() - opacity * speed, 0);
		GlStateManager.scale(size, size, 1);

		int color = Colors.setAlpha(Colors.WHITE, Math.round(opacity * 255));
		Bounds bounds = texture.align(Point.ZERO, Direction.CENTER);
		GlUtil.drawTexturedColoredModalRect(bounds.getX(), bounds.getY(), texture.getX(), texture.getY(), bounds.getWidth(), bounds.getHeight(), texture.getWidth(), texture.getHeight(), color);

		GlStateManager.popMatrix();
		GlMode.pop();
	}
}
