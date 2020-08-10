package jobicade.betterhud.element.particles;

import static jobicade.betterhud.BetterHud.MC;

import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.RandomWrapper;
import jobicade.betterhud.util.Textures;
import net.minecraft.client.gui.AbstractGui;

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
        MC.getTextureManager().bindTexture(Textures.HUD_ICONS);
        RenderSystem.pushMatrix();

        RenderSystem.translatef(position.getX(), position.getY(), 0.0F);
        RenderSystem.rotatef(rotation, 0, 0, 1);
        RenderSystem.scalef(this.size, this.size, 1.0F);

        Color color = Color.WHITE.withAlpha(Math.round(opacity * 255));
        Rect bounds = texture.align(Point.zero(), Direction.CENTER);
        GlUtil.drawRect(bounds, texture, color);

        RenderSystem.popMatrix();
        MC.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
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
