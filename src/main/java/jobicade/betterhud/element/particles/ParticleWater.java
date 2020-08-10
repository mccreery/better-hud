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

public class ParticleWater extends ParticleBase {
    private float speed;

    protected ParticleWater(Point position, int iconIndex, float opacity, float size, float speed) {
        super(position, 96 + iconIndex, opacity, size, 0);
        this.speed = speed;
    }

    public static ParticleWater createRandom() {
        RandomWrapper random = new RandomWrapper(new Random());

        Point position = random.nextPoint(Particle.getScreen());

        float opacity = random.nextFloat(0, 0.5f);
        float size = random.nextFloat(2, 6.5f);
        float speed = random.nextFloat(100, 350);

        int iconIndex = random.nextInt(2);
        return new ParticleWater(position, iconIndex, opacity, size, speed);
        //return new ParticleWater(new Point(10, 10), 0, 1.0f, 1.0f, 10.0f);
    }

    @Override
    public void render(float partialTicks) {
        MC.getTextureManager().bindTexture(Textures.PARTICLES);
        RenderSystem.pushMatrix();

        RenderSystem.translatef(position.getX(), position.getY() - opacity * speed, 0);
        RenderSystem.scalef(size, size, 1);

        Color color = Color.WHITE.withAlpha(Math.round(opacity * 255));
        Rect bounds = texture.align(Point.zero(), Direction.CENTER);
        GlUtil.drawRect(bounds, texture, color);

        RenderSystem.popMatrix();
        MC.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
    }
}
