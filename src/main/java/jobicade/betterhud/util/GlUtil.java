package jobicade.betterhud.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.Label;
import jobicade.betterhud.render.Quad;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.EnumSet;

public final class GlUtil {
    private GlUtil() {
    }

    /**
     * All axes default to {@code scale}
     *
     * @see GlStateManager#scale(float, float, float)
     */
    public static void scale(MatrixStack matrixStack, float scale) {
        matrixStack.scale(scale, scale, scale);
    }

    /** @see AbstractGui#drawRect(int, int, int, int, int) */
    public static void drawRect(Rect bounds, Color color) {
        new Quad().setColor(color).setBounds(bounds).render();
    }

    /**
     * Renders a rectangle with a texture.
     * @param bounds The outer bounding box of the rectangle.
     * @param texture The texture coordinates.
     */
    public static void drawRect(Rect bounds, Rect texture) {
        new Quad().setTexture(texture).setBounds(bounds).render();
    }

    /**
     * Renders a rectangle with a texture and a color.
     * @param bounds The outer bounding box of the rectangle.
     * @param texture The texture coordinates.
     * @param color The color of the rectangle.
     */
    public static void drawRect(Rect bounds, Rect texture, Color color) {
        new Quad().setTexture(texture).setColor(color).setBounds(bounds).render();
    }

    /**
     * Renders the edges of a rectangle.
     * @param bounds The outer bounding box of the rectangle.
     * @param color The color of the rectangle.
     */
    public static void drawBorderRect(Rect bounds, Color color) {
        Quad quad = new Quad().setColor(color);
        quad.setBounds(bounds.withWidth(1)).render();
        quad.setBounds(bounds.withHeight(1)).render();
        quad.setBounds(bounds.withLeft(bounds.getRight() - 1)).render();
        quad.setBounds(bounds.withTop(bounds.getBottom() - 1)).render();
    }

    /** Draws text with black borders on all sides */
    public static void drawBorderedString(MatrixStack matrixStack, String text, int x, int y, Color color) {
        // Borders
        Minecraft.getInstance().font.draw(matrixStack, text, x + 1, y, Color.BLACK.getPacked());
        Minecraft.getInstance().font.draw(matrixStack, text, x - 1, y, Color.BLACK.getPacked());
        Minecraft.getInstance().font.draw(matrixStack, text, x, y + 1, Color.BLACK.getPacked());
        Minecraft.getInstance().font.draw(matrixStack, text, x, y - 1, Color.BLACK.getPacked());

        Minecraft.getInstance().font.draw(matrixStack, text, x, y, color.getPacked());
        Color.WHITE.apply();
        RenderSystem.disableAlphaTest();
        Minecraft.getInstance().getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
    }

    /** @see #renderSingleItem(ItemStack, int, int) */
    public static void renderSingleItem(ItemStack stack, Point point) {
        renderSingleItem(stack, point.getX(), point.getY());
    }

    /** Renders {@code stack} to the GUI, and reverts lighting side effects
     * OpenGL side-effect: disables depth and item lighting
     *
     * @see RenderHelper#enableGUIStandardItemLighting()
     * @see net.minecraft.client.renderer.RenderItem#renderItemAndEffectIntoGUI(ItemStack, int, int)
     * @see RenderHelper#disableStandardItemLighting() */
    public static void renderSingleItem(ItemStack stack, int x, int y) {
        RenderSystem.enableDepthTest();
        RenderHelper.turnBackOn();
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y);
        RenderHelper.turnOff();
        RenderSystem.disableDepthTest();
        Minecraft.getInstance().getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
        blendFuncSafe(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
    }

    /** Renders the item with hotbar animations.
     * OpenGL side-effects: disables depth and item ligthing.
     */
    public static void renderHotbarItem(MatrixStack matrixStack, Rect bounds, ItemStack stack, float partialTicks) {
        if(stack.isEmpty()) return;
        float animationTicks = stack.getPopTime() - partialTicks;

        RenderSystem.enableDepthTest();
        RenderHelper.turnBackOn();
        if(animationTicks > 0) {
            float factor = 1 + animationTicks / 5;

            matrixStack.pushPose();
            matrixStack.translate(bounds.getX() + 8, bounds.getY() + 12, 0);
            matrixStack.scale(1 / factor, (factor + 1) / 2, 1);
            matrixStack.translate(-(bounds.getX() + 8), -(bounds.getY() + 12), 0.0F);

            Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(Minecraft.getInstance().player, stack, bounds.getX(), bounds.getY());

            matrixStack.popPose();
        } else {
            Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(Minecraft.getInstance().player, stack, bounds.getX(), bounds.getY());
        }

        Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(Minecraft.getInstance().font, stack, bounds.getX(), bounds.getY());

        // Possible side-effects
        RenderHelper.turnOff();
        RenderSystem.disableDepthTest();
        RenderSystem.disableAlphaTest();
        Minecraft.getInstance().getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
        GlUtil.blendFuncSafe(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
    }

    /**
     * Draws a box resembling an item tooltip.
     * @param bounds The bounding box of the tooltip box.
     */
    public static void drawTooltipBox(Rect bounds) {
        Color background   = new Color(183, 16, 0, 16);
        Color borderTop    = new Color(80, 80, 0, 255);
        Color borderBottom = new Color(80, 40, 0, 127);

        Quad quad = new Quad().setZLevel(300);

        // Box
        quad.setColor(background);
        quad.setBounds(bounds.withHeight(1).grow(-1, 0, -1, 0)).render();
        quad.setBounds(bounds.grow(0, -1, 0, -1)).render();
        quad.setBounds(bounds.withTop(bounds.getBottom() - 1).grow(-1, 0, -1, 0)).render();

        // Borders
        Rect inner = bounds.grow(-1);
        quad.setColor(borderTop).setBounds(inner.withHeight(1)).render();
        quad.setColor(borderBottom).setBounds(inner.withTop(inner.getBottom() - 1)).render();

        // Sides
        inner = inner.grow(0, -1, 0, -1);
        quad.setColors(borderTop, borderTop, borderBottom, borderBottom);
        quad.setBounds(inner.withWidth(1)).render();
        quad.setBounds(inner.withLeft(inner.getRight() - 1)).render();
    }

    /** Applies transformations such that the Z axis faces directly towards the player
     * and (0, 0) is translated to above {@code entity}'s head.
     * <p>This is similar to the method used to render player names, but any functionality can be implemented
     *
     * @param scaleFactor Linearly affects the size of things drawn to the billboard
     * @see net.minecraft.client.renderer.EntityRenderer#drawNameplate(net.minecraft.client.gui.FontRenderer, String, float, float, float, int, float, float, boolean, boolean) */
    public static void setupBillboard(MatrixStack matrixStack, Entity entity, float partialTicks, float scaleFactor) {
        double dx = (entity.xo + (entity.getX() - entity.xo) * partialTicks) - (Minecraft.getInstance().player.xo + (Minecraft.getInstance().player.getX() - Minecraft.getInstance().player.xo) * partialTicks);
        double dy = (entity.yo + (entity.getY() - entity.yo) * partialTicks) - (Minecraft.getInstance().player.yo + (Minecraft.getInstance().player.getY() - Minecraft.getInstance().player.yo) * partialTicks);
        double dz = (entity.zo + (entity.getZ() - entity.zo) * partialTicks) - (Minecraft.getInstance().player.zo + (Minecraft.getInstance().player.getZ() - Minecraft.getInstance().player.zo) * partialTicks);

        dy += entity.getBbHeight() + 0.5;
        matrixStack.translate(dx, dy, dz);

        dy -= Minecraft.getInstance().player.getEyeHeight();
        float distance = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
        scale(matrixStack, distance * (scaleFactor + 0.5f) / 300f);

        matrixStack.mulPose(new Quaternion(new Vector3f(0, 1, 0), -Minecraft.getInstance().player.yRot, true));
        matrixStack.mulPose(new Quaternion(new Vector3f(1, 0, 0), Minecraft.getInstance().player.xRot, true));
        matrixStack.mulPose(new Quaternion(new Vector3f(0, 0, 1), 180, true));
    }

    /** {@code progress} defaults to the durability of {@code stack}
     * @see #drawProgressBar(Rect, float, boolean) */
    public static void drawDamageBar(Rect bounds, ItemStack stack, boolean vertical) {
        float progress = (float)(stack.getMaxDamage() - stack.getDamageValue()) / stack.getMaxDamage();
        drawProgressBar(bounds, progress, vertical);
    }

    /** Draws a progress bar for item damage
     * @param progress Index of progress between 0 and 1
     * @param vertical {@code true} to render bar from bottom to top */
    public static void drawProgressBar(Rect bounds, float progress, boolean vertical) {
        drawRect(bounds, Color.BLACK);
        progress = MathHelper.clamp(progress, 0, 1);

        Color color = Color.fromHSV(progress / 3, 1, 1);

        Rect bar;
        if(vertical) {
            bar = new Rect(bounds.getWidth() - 1, (int)(progress * bounds.getHeight()));
            bar = bar.anchor(bounds, Direction.SOUTH_WEST);
        } else {
            bar = new Rect((int)(progress * bounds.getWidth()), bounds.getHeight() - 1);
            bar = bar.anchor(bounds, Direction.NORTH_WEST);
        }
        drawRect(bar, color);
    }

    /** Draws a progress bar with textures
     * @param progress Index of progress between 0 and 1
     * @param direction The direction the bar should fill up in */
    public static void drawTexturedProgressBar(Point position, Rect background, Rect foreground, float progress, Direction direction) {
        drawRect(background.move(position), background);

        Rect bounds = background.move(position);
        Rect partialRect = new Rect(bounds);
        Rect partialForeground = new Rect(foreground);

        if(!DirectionOptions.VERTICAL.isValid(direction)) {
            int partial = MathHelper.ceil(progress * partialRect.getWidth());

            partialRect = partialRect.withWidth(partial);
            partialForeground = partialForeground.withWidth(partial);
        } else {
            int partial = MathHelper.ceil(progress * partialRect.getHeight());

            partialRect = partialRect.withHeight(partial);
            partialForeground = partialForeground.withHeight(partial);
        }

        Direction anchor = direction.mirror();
        partialRect = partialRect.anchor(bounds, anchor);
        partialForeground = partialForeground.anchor(foreground, anchor);

        drawRect(partialRect, partialForeground);
    }

    /** @return The size of {@code string} as rendered by Minecraft's font renderer */
    public static Point getStringSize(String string) {
        return new Point(Minecraft.getInstance().font.width(string), Minecraft.getInstance().font.lineHeight);
    }

    /**
     * OpenGL side-effect: color set to white, texture set to Gui.ICONS
     * @see #drawString(String, Point, Direction, int)
     */
    public static Rect drawString(String string, Point origin, Direction alignment, Color color) {
        Label label = new Label(string).setColor(color);
        Rect bounds = new Rect(label.getPreferredSize()).align(origin, alignment);
        label.setBounds(bounds).render();
        return bounds;
    }

    /**
     * Fixes a GlStateManager bug where calling {@link GlStateManager#blendFunc(SourceFactor, DestFactor)}
     * causes a desync, and can ignore calls to {@link GlStateManager#tryBlendFuncSeparate(SourceFactor, DestFactor, SourceFactor, DestFactor)}
     * when the cache thinks srcFactorAlpha and dstFactorAlpha haven't been changed by blendFunc (they have).
     *
     * <p>Fix in vanilla: add these lines:
     * <p><blockquote><pre>
     * public static void blendFunc(int srcFactor, int dstFactor)
     * {
     *     if (srcFactor != blendState.srcFactor || dstFactor != blendState.dstFactor)
     *     {
     *         blendState.srcFactor = srcFactor;
     *         blendState.dstFactor = dstFactor;
     *         blendState.srcFactorAlpha = srcFactor;
     *         blendState.dstFactorAlpha = dstFactor;
     *         GL11.glBlendFunc(srcFactor, dstFactor);
     *     }
     * }
     * </pre></blockquote></p>
     */
    public static void blendFuncSafe(SourceFactor srcFactor, DestFactor dstFactor, SourceFactor srcFactorAlpha, DestFactor dstFactorAlpha) {
        // We need to trick the state manager into updating the cache
        EnumSet<SourceFactor> factors = EnumSet.allOf(SourceFactor.class);
        factors.remove(srcFactor);
        factors.removeIf(f -> f.value == GL11.glGetInteger(GL11.GL_BLEND_SRC));

        // Get a factor which is distinct from both current and new factor
        SourceFactor dummyFactor = factors.iterator().next();
        // Ensure cache differs from our desired values
        RenderSystem.blendFuncSeparate(dummyFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
        // Guarantee cache updates correctly
        RenderSystem.blendFuncSeparate(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
    }

    public static void beginScissor(Rect scissorRect, MainWindow mainWindow) {
        final Rect scaledRect = scissorRect
            .withY(mainWindow.getGuiScaledHeight() - scissorRect.getBottom())
            .scale((int)mainWindow.getGuiScale());

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scaledRect.getX(), scaledRect.getY(), scaledRect.getWidth(), scaledRect.getHeight());
    }

    public static void endScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
