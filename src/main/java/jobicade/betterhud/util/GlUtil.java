package jobicade.betterhud.util;

import static jobicade.betterhud.BetterHud.MC;

import java.util.EnumSet;
import java.util.List;

import org.lwjgl.opengl.GL11;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.Quad;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiUtils;

public final class GlUtil {
	private GlUtil() {
	}

	/**
	 * All axes default to {@code scale}
	 *
	 * @see GlStateManager#scale(float, float, float)
	 */
	public static void scale(float scale) {
		GlStateManager.scale(scale, scale, scale);
	}

	/** @see Gui#drawRect(int, int, int, int, int) */
	public static void drawRect(Rect bounds, Color color) {
		Quad quad = new Quad(bounds);
		quad.setColor(color);
		quad.render();
	}

	/**
	 * Renders a rectangle with a texture.
	 * @param bounds The outer bounding box of the rectangle.
	 * @param texture The texture coordinates.
	 */
	public static void drawRect(Rect bounds, Rect texture) {
		Quad quad = new Quad(bounds);
		quad.setTexture(texture);
		quad.render();
	}

	/**
	 * Renders a rectangle with a texture and a color.
	 * @param bounds The outer bounding box of the rectangle.
	 * @param texture The texture coordinates.
	 * @param color The color of the rectangle.
	 */
	public static void drawRect(Rect bounds, Rect texture, Color color) {
		Quad quad = new Quad(bounds);
		quad.setTexture(texture);
		quad.setColor(color);
		quad.render();
	}

	/**
	 * Renders the edges of a rectangle.
	 * @param bounds The outer bounding box of the rectangle.
	 * @param color The color of the rectangle.
	 */
	public static void drawBorderRect(Rect bounds, Color color) {
		Quad quad = new Quad(bounds.withWidth(1));
		quad.setColor(color);
		quad.render();

		quad.setBounds(bounds.withHeight(1));
		quad.render();
		quad.setBounds(bounds.withLeft(bounds.getRight() - 1));
		quad.render();
		quad.setBounds(bounds.withTop(bounds.getBottom() - 1));
		quad.render();
	}

	/** Draws text with black borders on all sides */
	public static void drawBorderedString(String text, int x, int y, Color color) {
		// Borders
		MC.fontRenderer.drawString(text, x + 1, y, Color.BLACK.getPacked(), false);
		MC.fontRenderer.drawString(text, x - 1, y, Color.BLACK.getPacked(), false);
		MC.fontRenderer.drawString(text, x, y + 1, Color.BLACK.getPacked(), false);
		MC.fontRenderer.drawString(text, x, y - 1, Color.BLACK.getPacked(), false);

		MC.fontRenderer.drawString(text, x, y, color.getPacked(), false);
		Color.WHITE.apply();
		MC.getTextureManager().bindTexture(Gui.ICONS);
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
		GlStateManager.enableDepth();
		RenderHelper.enableGUIStandardItemLighting();
		MC.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableDepth();
		MC.getTextureManager().bindTexture(Gui.ICONS);
		blendFuncSafe(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
	}

	/** Renders the item with hotbar animations.
	 * OpenGL side-effects: disables depth and item ligthing.
	 */
	public static void renderHotbarItem(Rect bounds, ItemStack stack, float partialTicks) {
		if(stack.isEmpty()) return;
		float animationTicks = stack.getAnimationsToGo() - partialTicks;

		GlStateManager.enableDepth();
		RenderHelper.enableGUIStandardItemLighting();
		if(animationTicks > 0) {
			float factor = 1 + animationTicks / 5;

			GlStateManager.pushMatrix();
			GlStateManager.translate(bounds.getX() + 8, bounds.getY() + 12, 0);
			GlStateManager.scale(1 / factor, (factor + 1) / 2, 1);
			GlStateManager.translate(-(bounds.getX() + 8), -(bounds.getY() + 12), 0.0F);

			MC.getRenderItem().renderItemAndEffectIntoGUI(MC.player, stack, bounds.getX(), bounds.getY());

			GlStateManager.popMatrix();
		} else {
			MC.getRenderItem().renderItemAndEffectIntoGUI(MC.player, stack, bounds.getX(), bounds.getY());
		}

		MC.getRenderItem().renderItemOverlays(MC.fontRenderer, stack, bounds.getX(), bounds.getY());

		// Possible side-effects
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableDepth();
		MC.getTextureManager().bindTexture(Gui.ICONS);
	}

	/** @see GuiUtils#drawHoveringText(ItemStack, List, int, int, int, int, int, net.minecraft.client.gui.FontRenderer) */
	public static void drawTooltipBox(int x, int y, int w, int h) {
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();

		final int zLevel	  = 300;
		final int bgColor	  = 0xb7100010;
		final int borderStart = 0x505000ff;
		final int borderEnd   = (borderStart & 0xfefefe) >> 1 | borderStart & 0xff000000;

		// Box
		GuiUtils.drawGradientRect(zLevel, x+1, y,	 x+w-1, y+1,   bgColor, bgColor); // Top
		GuiUtils.drawGradientRect(zLevel, x,   y+1,   x+w,   y+h-1, bgColor, bgColor); // Middle
		GuiUtils.drawGradientRect(zLevel, x+1, y+h-1, x+w-1, y+h,   bgColor, bgColor); // Bottom

		// Borders
		GuiUtils.drawGradientRect(zLevel, x+1,   y+1,   x+w-1, y+2,   borderStart, borderStart); // Top
		GuiUtils.drawGradientRect(zLevel, x+1,   y+2,   x+2,   y+h-2, borderStart, borderEnd);   // Left
		GuiUtils.drawGradientRect(zLevel, x+w-2, y+2,   x+w-1, y+h-2, borderStart, borderEnd);   // Right
		GuiUtils.drawGradientRect(zLevel, x+1,   y+h-2, x+w-1, y+h-1, borderEnd,   borderEnd);   // Bottom

		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
	}

	/** Applies transformations such that the Z axis faces directly towards the player
	 * and (0, 0) is translated to above {@code entity}'s head.
	 * <p>This is similar to the method used to render player names, but any functionality can be implemented
	 *
	 * @param scaleFactor Linearly affects the size of things drawn to the billboard
	 * @see net.minecraft.client.renderer.EntityRenderer#drawNameplate(net.minecraft.client.gui.FontRenderer, String, float, float, float, int, float, float, boolean, boolean) */
	public static void setupBillboard(Entity entity, float partialTicks, float scaleFactor) {
		double dx = (entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks) - (MC.player.prevPosX + (MC.player.posX - MC.player.prevPosX) * partialTicks);
		double dy = (entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks) - (MC.player.prevPosY + (MC.player.posY - MC.player.prevPosY) * partialTicks);
		double dz = (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks) - (MC.player.prevPosZ + (MC.player.posZ - MC.player.prevPosZ) * partialTicks);

		dy += entity.height + 0.5;
		GlStateManager.translate(dx, dy, dz);

		dy -= MC.player.getEyeHeight();
		float distance = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
		scale(distance * (scaleFactor + 0.5f) / 300f);

		GlStateManager.rotate(-MC.player.rotationYaw,  0, 1, 0);
		GlStateManager.rotate(MC.player.rotationPitch, 1, 0, 0);
		GlStateManager.rotate(180, 0, 0, 1);
	}

	/** {@code progress} defaults to the durability of {@code stack}
	 * @see #drawProgressBar(Rect, float, boolean) */
	public static void drawDamageBar(Rect bounds, ItemStack stack, boolean vertical) {
		float progress = (float)(stack.getMaxDamage() - stack.getItemDamage()) / stack.getMaxDamage();
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
		return new Point(MC.fontRenderer.getStringWidth(string), MC.fontRenderer.FONT_HEIGHT);
	}

	/**
	 * OpenGL side-effect: color set to white, texture set to Gui.ICONS
	 * @see #drawString(String, Point, Direction, int)
	 */
	public static Rect drawString(String string, Point origin, Direction alignment, Color color) {
		Rect bounds = new Rect(getStringSize(string)).align(origin, alignment);
		MC.fontRenderer.drawStringWithShadow(string, bounds.getX(), bounds.getY(), color.getPacked());

		Color.WHITE.apply();
		MC.getTextureManager().bindTexture(Gui.ICONS);
		GlStateManager.disableAlpha();
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
		factors.removeIf(f -> f.factor == GL11.glGetInteger(GL11.GL_BLEND_SRC));

		// Get a factor which is distinct from both current and new factor
		SourceFactor dummyFactor = factors.iterator().next();
		// Ensure cache differs from our desired values
		GlStateManager.tryBlendFuncSeparate(dummyFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
		// Guarantee cache updates correctly
		GlStateManager.tryBlendFuncSeparate(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
	}
}
