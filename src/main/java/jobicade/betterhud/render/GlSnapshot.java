package jobicade.betterhud.render;

import java.nio.FloatBuffer;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

/**
 * Data class containing a single immutable snapshot of the OpenGL state.
 * Can be used to check that a state has not changed between two points.
 */
public class GlSnapshot {
    private final Color color;
    private final int texture;
    private final BlendFunc blendFunc;
    private final Set<GlFlag> flags;

    public GlSnapshot() {
        color = getCurrentColor();
        texture = getCurrentTexture();
        blendFunc = getCurrentBlendFunc();

        flags = EnumSet.noneOf(GlFlag.class);

        for (GlFlag flag : GlFlag.values()) {
            if (flag.isEnabled()) {
                flags.add(flag);
            }
        }
    }

    public GlSnapshot(Color color, int texture, BlendFunc blendFunc, GlFlag... flags) {
        this.color = color;
        this.texture = texture;
        this.blendFunc = blendFunc;

        if (flags.length == 0) {
            this.flags = EnumSet.noneOf(GlFlag.class);
        } else {
            this.flags = EnumSet.of(flags[0], flags);
        }
    }

    public void apply() {
        GlStateManager.color4f(
            color.getRed() / 255.0f,
            color.getGreen() / 255.0f,
            color.getBlue() / 255.0f,
            1.0f);

        GlStateManager.bindTexture(texture);

        GlStateManager.blendFuncSeparate(
            blendFunc.getSrcFactor().param,
            blendFunc.getDstFactor().param,
            blendFunc.getSrcFactorAlpha().param,
            blendFunc.getDstFactorAlpha().param);

        for (GlFlag flag : GlFlag.values()) {
            flag.setEnabled(flags.contains(flag));
        }
    }

    private Color getCurrentColor() {
        FloatBuffer buf = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloatv(GL11.GL_CURRENT_COLOR, buf);

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

        public BlendFunc(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {
            this(getSrcFactor(srcFactor), getDstFactor(dstFactor), getSrcFactor(srcFactorAlpha), getDstFactor(dstFactorAlpha));
        }

        public BlendFunc(SourceFactor srcFactor, DestFactor dstFactor, SourceFactor srcFactorAlpha, DestFactor dstFactorAlpha) {
            this.srcFactor = srcFactor;
            this.dstFactor = dstFactor;
            this.srcFactorAlpha = srcFactorAlpha;
            this.dstFactorAlpha = dstFactorAlpha;
        }

        private static SourceFactor getSrcFactor(int factor) {
            for(SourceFactor srcFactor : SourceFactor.values()) {
                if(srcFactor.param == factor) return srcFactor;
            }
            return null;
        }

        private static DestFactor getDstFactor(int factor) {
            for(DestFactor dstFactor : DestFactor.values()) {
                if(dstFactor.param == factor) return dstFactor;
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
