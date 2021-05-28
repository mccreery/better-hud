package jobicade.betterhud.element.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.MathUtil;
import jobicade.betterhud.util.Textures;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.render.Color;

public class ParticleBase implements Particle {
    protected Point position;
    protected Rect texture;

    protected float opacity, size, rotation;

    protected ParticleBase(Point position, int iconIndex, float opacity, float size, float rotation) {
        this(position, new Rect((iconIndex % 16) * 16, (iconIndex / 16) * 16, 16, 16), opacity, size, rotation);
    }

    protected ParticleBase(Point position, Rect texture, float opacity, float size, float rotation) {
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
        Minecraft.getInstance().getTextureManager().bind(Textures.HUD_ICONS);
        GlStateManager.func_179094_E();

        GlStateManager.func_179109_b(position.getX(), position.getY(), 0.0F);
        GlStateManager.func_179114_b(rotation, 0, 0, 1);
        GlStateManager.func_179152_a(this.size, this.size, 1.0F);

        Color color = Color.WHITE.withAlpha(Math.round(opacity * 255));
        Rect bounds = texture.align(Point.zero(), Direction.CENTER);
        GlUtil.drawRect(bounds, texture, color);

        GlStateManager.func_179121_F();
        Minecraft.getInstance().getTextureManager().bind(Gui.field_110324_m);
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
