package jobicade.betterhud.render;

import com.mojang.blaze3d.systems.RenderSystem;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public enum GlFlag {
    ALPHA_TEST(GL11.GL_ALPHA_TEST, RenderSystem::enableAlphaTest, RenderSystem::disableAlphaTest),
    BLEND(GL11.GL_BLEND, RenderSystem::enableBlend, RenderSystem::disableBlend),
    DEPTH_TEST(GL11.GL_DEPTH_TEST, RenderSystem::enableDepthTest, RenderSystem::disableDepthTest),
    LIGHTING(GL11.GL_LIGHTING, RenderSystem::enableLighting, RenderSystem::disableLighting),
    TEXTURE_2D(GL11.GL_TEXTURE_2D, RenderSystem::enableTexture, RenderSystem::disableTexture),
    COLOR_MATERIAL(GL11.GL_COLOR_MATERIAL, RenderSystem::enableColorMaterial, RenderSystem::disableColorMaterial),
    RESCALE_NORMAL(GL12.GL_RESCALE_NORMAL, RenderSystem::enableRescaleNormal, RenderSystem::disableRescaleNormal);

    private final int code;
    private final Runnable enableCallback;
    private final Runnable disableCallback;

    GlFlag(int code, Runnable enableCallback, Runnable disableCallback) {
        this.code = code;
        this.enableCallback = enableCallback;
        this.disableCallback = disableCallback;
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            enableCallback.run();
        } else {
            disableCallback.run();
        }
    }

    public boolean isEnabled() {
        return GL11.glIsEnabled(code);
    }
}
