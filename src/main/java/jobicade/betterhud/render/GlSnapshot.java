package jobicade.betterhud.render;

import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;

import net.minecraft.client.renderer.GlStateManager;

/**
 * Data class containing a single immutable snapshot of the OpenGL state.
 * Can be used to check that a state has not changed between two points.
 */
public class GlSnapshot {
    private final Map<GlFlag, Boolean> flags = new EnumMap<>(GlFlag.class);
    private final Color color;
    private final int texture;
    private final BlendFunc blendFunc;

    public GlSnapshot() {
        for(GlFlag flag : GlFlag.values()) {
            this.flags.put(flag, flag.isEnabled());
        }
        this.color = getCurrentColor();
        this.texture = getCurrentTexture();
        this.blendFunc = getCurrentBlendFunc();
    }

    public void apply() {
        for (GlFlag flag : flags.keySet()) {
            flag.setEnabled(flags.get(flag));
        }
        GlStateManager.color(
            color.getRed() / 255.0f,
            color.getGreen() / 255.0f,
            color.getBlue() / 255.0f);

        GlStateManager.bindTexture(texture);

        GlStateManager.tryBlendFuncSeparate(
            blendFunc.getSrcFactor(),
            blendFunc.getDstFactor(),
            blendFunc.getSrcFactorAlpha(),
            blendFunc.getDstFactorAlpha());
    }

    private Color getCurrentColor() {
        FloatBuffer buf = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(GL11.GL_CURRENT_COLOR, buf);

        int red   = Math.round(buf.get(0) * 255.0f);
        int green = Math.round(buf.get(1) * 255.0f);
        int blue  = Math.round(buf.get(2) * 255.0f);
        int alpha = Math.round(buf.get(3) * 255.0f);

        // Note ARGB (Minecraft)/RGBA (OpenGL)
        return new Color(alpha, red, green, blue);
    }

    private int getCurrentTexture() {
        return GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
    }

    private BlendFunc getCurrentBlendFunc() {
        int srcFactor = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
        int dstFactor = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
        int srcFactorAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
        int dstFactorAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);

        return new BlendFunc(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
    }

    public Map<GlFlag, Boolean> getFlags() {
        return Collections.unmodifiableMap(flags);
    }

    public Color getColor() {
        return color;
    }

    public int getTexture() {
        return texture;
    }

    public BlendFunc getBlendFunc() {
        return blendFunc;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof GlSnapshot)) return false;
        GlSnapshot snapshot = (GlSnapshot)obj;

        return flags.equals(snapshot.flags) &&
            color.equals(snapshot.color) &&
            texture == snapshot.texture &&
            blendFunc.equals(snapshot.blendFunc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flags, color, texture, blendFunc);
    }

    @Override
    public String toString() {
        return String.format("{flags: %s, color: %s, texture: %d, blendfunc: %s}", flags, color, texture, blendFunc);
    }

    public static class BlendFunc {
        private final SourceFactor srcFactor;
        private final DestFactor dstFactor;
        private final SourceFactor srcFactorAlpha;
        private final DestFactor dstFactorAlpha;

        private BlendFunc(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {
            this(getSrcFactor(srcFactor), getDstFactor(dstFactor), getSrcFactor(srcFactorAlpha), getDstFactor(dstFactorAlpha));
        }

        private BlendFunc(SourceFactor srcFactor, DestFactor dstFactor, SourceFactor srcFactorAlpha, DestFactor dstFactorAlpha) {
            this.srcFactor = srcFactor;
            this.dstFactor = dstFactor;
            this.srcFactorAlpha = srcFactorAlpha;
            this.dstFactorAlpha = dstFactorAlpha;
        }

        private static SourceFactor getSrcFactor(int factor) {
            for(SourceFactor srcFactor : SourceFactor.values()) {
                if(srcFactor.factor == factor) return srcFactor;
            }
            return null;
        }

        private static DestFactor getDstFactor(int factor) {
            for(DestFactor dstFactor : DestFactor.values()) {
                if(dstFactor.factor == factor) return dstFactor;
            }
            return null;
        }

        public SourceFactor getSrcFactor() { return srcFactor; }
        public DestFactor getDstFactor() { return dstFactor; }
        public SourceFactor getSrcFactorAlpha() { return srcFactorAlpha; }
        public DestFactor getDstFactorAlpha() { return dstFactorAlpha; }

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof BlendFunc)) return false;
            BlendFunc blendFunc = (BlendFunc)obj;

            return srcFactor == blendFunc.srcFactor &&
                dstFactor == blendFunc.dstFactor &&
                srcFactorAlpha == blendFunc.srcFactorAlpha &&
                dstFactorAlpha == blendFunc.dstFactorAlpha;
        }

        @Override
        public int hashCode() {
            return Objects.hash(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
        }

        @Override
        public String toString() {
            return String.format("{src: %s, dst: %s, srcAlpha: %s, dstAlpha: %s}", srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
        }
    }
}
