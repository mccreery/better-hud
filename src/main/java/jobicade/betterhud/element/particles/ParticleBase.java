package jobicade.betterhud.element.particles;

import java.util.Random;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.RandomWrapper;
import jobicade.betterhud.util.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

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
        RandomWrapper random = new RandomWrapper(new Random());

        Point position = random.nextPoint(Particle.getScreen());

        float opacity = random.nextFloat(0, 0.5f);
        float size = random.nextFloat(2, 6);
        float rotation = random.nextFloat(0, 360);

        int iconIndex = random.nextInt(16);
        return new ParticleBase(position, iconIndex, opacity, size, rotation);
    }

    @Override
    public boolean shouldRender() {
        return !isDead();
    }

    @Override
    public void render(float partialTicks) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(Textures.HUD_ICONS);
        GlStateManager.pushMatrix();

        GlStateManager.translate(position.getX(), position.getY(), 0.0F);
        GlStateManager.rotate(rotation, 0, 0, 1);
        GlStateManager.scale(this.size, this.size, 1.0F);

        Color color = Color.WHITE.withAlpha(Math.round(opacity * 255));
        Rect bounds = texture.align(Point.zero(), Direction.CENTER);
        GlUtil.drawRect(bounds, texture, color);

        GlStateManager.popMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
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
