package jobicade.betterhud.render;

import com.mojang.blaze3d.platform.GlStateManager;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public enum GlFlag {
    ALPHA_TEST(GL11.GL_ALPHA_TEST, GlStateManager::enableAlphaTest, GlStateManager::disableAlphaTest),
    BLEND(GL11.GL_BLEND, GlStateManager::enableBlend, GlStateManager::disableBlend),
    DEPTH_TEST(GL11.GL_DEPTH_TEST, GlStateManager::enableDepthTest, GlStateManager::disableDepthTest),
    LIGHTING(GL11.GL_LIGHTING, GlStateManager::enableLighting, GlStateManager::disableLighting),
    TEXTURE_2D(GL11.GL_TEXTURE_2D, GlStateManager::enableTexture, GlStateManager::disableTexture),
    COLOR_MATERIAL(GL11.GL_COLOR_MATERIAL, GlStateManager::enableColorMaterial, GlStateManager::disableColorMaterial),
    RESCALE_NORMAL(GL12.GL_RESCALE_NORMAL, GlStateManager::enableRescaleNormal, GlStateManager::disableRescaleNormal),

    LIGHT0(GL11.GL_LIGHT0, () -> GlStateManager.enableLight(0), () -> {}),
    LIGHT1(GL11.GL_LIGHT1, () -> GlStateManager.enableLight(1), () -> {}),
    LIGHT2(GL11.GL_LIGHT2, () -> GlStateManager.enableLight(2), () -> {}),
    LIGHT3(GL11.GL_LIGHT3, () -> GlStateManager.enableLight(3), () -> {}),
    LIGHT4(GL11.GL_LIGHT4, () -> GlStateManager.enableLight(4), () -> {}),
    LIGHT5(GL11.GL_LIGHT5, () -> GlStateManager.enableLight(5), () -> {}),
    LIGHT6(GL11.GL_LIGHT6, () -> GlStateManager.enableLight(6), () -> {}),
    LIGHT7(GL11.GL_LIGHT7, () -> GlStateManager.enableLight(7), () -> {});

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
