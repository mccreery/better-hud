package jobicade.betterhud.render;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;

import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Data class containing a single immutable snapshot of the OpenGL state.
 * Can be used to check that a state has not changed between two points.
 */
public class GlSnapshot {
    private final Map<Flag, Boolean> flags = new EnumMap<>(Flag.class);
    private final Color color;
    private final int texture;
    private final BlendFunc blendFunc;

    public GlSnapshot() {
        for(Flag flag : Flag.values()) {
            this.flags.put(flag, flag.isEnabled());
        }
        this.color = getCurrentColor();
        this.texture = getCurrentTexture();
        this.blendFunc = getCurrentBlendFunc();
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

    public Map<Flag, Boolean> getFlags() {
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
                if(srcFactor.field_187395_p == factor) return srcFactor;
            }
            return null;
        }

        private static DestFactor getDstFactor(int factor) {
            for(DestFactor dstFactor : DestFactor.values()) {
                if(dstFactor.field_187345_o == factor) return dstFactor;
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

    public enum Flag {
        ALPHA_TEST(GL11.GL_ALPHA_TEST),
        BLEND(GL11.GL_BLEND),
        DEPTH_TEST(GL11.GL_DEPTH_TEST),
        LIGHTING(GL11.GL_LIGHTING),
        TEXTURE_2D(GL11.GL_TEXTURE_2D),
        COLOR_MATERIAL(GL11.GL_COLOR_MATERIAL),
        RESCALE_NORMAL(GL12.GL_RESCALE_NORMAL),

        LIGHT_0(GL11.GL_LIGHT0),
        LIGHT_1(GL11.GL_LIGHT1),
        LIGHT_2(GL11.GL_LIGHT2),
        LIGHT_3(GL11.GL_LIGHT3),
        LIGHT_4(GL11.GL_LIGHT4),
        LIGHT_5(GL11.GL_LIGHT5),
        LIGHT_6(GL11.GL_LIGHT6),
        LIGHT_7(GL11.GL_LIGHT7);

        private final int code;

        Flag(int code) {
            this.code = code;
        }

        public boolean isEnabled() {
            return GL11.glIsEnabled(code);
        }
    }
}
