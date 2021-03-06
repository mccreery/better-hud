package jobicade.betterhud.element.particles;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.MathUtil;
import jobicade.betterhud.util.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

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
        //return new ParticleWater(new Point(10, 10), 0, 1.0f, 1.0f, 10.0f);
    }

    @Override
    public void render(float partialTicks) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(Textures.PARTICLES);
        GlStateManager.pushMatrix();

        GlStateManager.translate(position.getX(), position.getY() - opacity * speed, 0);
        GlStateManager.scale(size, size, 1);

        Color color = Color.WHITE.withAlpha(Math.round(opacity * 255));
        Rect bounds = texture.align(Point.zero(), Direction.CENTER);
        GlUtil.drawRect(bounds, texture, color);

        GlStateManager.popMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
    }
}
