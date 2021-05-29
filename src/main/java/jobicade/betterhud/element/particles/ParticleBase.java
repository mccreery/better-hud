package jobicade.betterhud.element.particles;

import com.mojang.blaze3d.matrix.MatrixStack;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.MathUtil;
import jobicade.betterhud.util.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

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
    public void render(MatrixStack matrixStack, float partialTicks) {
        Minecraft.getInstance().getTextureManager().bind(Textures.HUD_ICONS);
        matrixStack.pushPose();

        matrixStack.translate(position.getX(), position.getY(), 0.0F);
        matrixStack.mulPose(new Quaternion(new Vector3f(0, 0, 1), rotation, true));
        matrixStack.scale(this.size, this.size, 1.0F);

        Color color = Color.WHITE.withAlpha(Math.round(opacity * 255));
        Rect bounds = texture.align(Point.zero(), Direction.CENTER);
        GlUtil.drawRect(bounds, texture, color);

        matrixStack.popPose();
        Minecraft.getInstance().getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
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
