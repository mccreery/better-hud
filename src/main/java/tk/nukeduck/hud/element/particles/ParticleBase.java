package tk.nukeduck.hud.element.particles;

import static tk.nukeduck.hud.BetterHud.HUD_ICONS;

import net.minecraft.client.renderer.GlStateManager;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.MathUtil;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.mode.GlMode;
import tk.nukeduck.hud.util.mode.TextureMode;

public class ParticleBase implements Particle {
	protected Point position;
	protected Bounds texture;

	protected float opacity, size, rotation;

	protected ParticleBase(Point position, int iconIndex, float opacity, float size, float rotation) {
		this(position, new Bounds((iconIndex % 16) * 16, (iconIndex / 16) * 16, 16, 16), opacity, size, rotation);
	}

	protected ParticleBase(Point position, Bounds texture, float opacity, float size, float rotation) {
		this.position = position;
		this.texture = texture;

		this.opacity = opacity;
		this.size = size;
		this.rotation = rotation;
	}

	public static ParticleBase createRandom() {
		Point position = MathUtil.randomPoint(Particle.getScreen());

		float opacity = MathUtil.randomRange(0f, 0.5f);
		float size = MathUtil.randomRange(2f, 6f);
		float rotation = MathUtil.randomRange(0f, 360f);

		int iconIndex = MathUtil.randomRange(0, 16);
		return new ParticleBase(position, iconIndex, opacity, size, rotation);
	}

	@Override
	public boolean shouldRender() {
		return !isDead();
	}

	@Override
	public void render(float partialTicks) {
		GlMode.push(new TextureMode(HUD_ICONS));
		GlStateManager.pushMatrix();

		GlStateManager.translate(position.getX(), position.getY(), 0.0F);
		GlStateManager.rotate(rotation, 0, 0, 1);
		GlStateManager.scale(this.size, this.size, 1.0F);
		GlUtil.drawTexturedModalRect(Point.ZERO, texture);

		GlStateManager.popMatrix();
		GlMode.pop();
	}

	@Override
	public void tick() {
		opacity -= 0.003;
	}

	@Override
	public boolean isDead() {
		return opacity <= 0;
	}
}
