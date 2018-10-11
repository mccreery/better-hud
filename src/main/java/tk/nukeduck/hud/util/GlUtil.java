package tk.nukeduck.hud.util;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.util.mode.GlMode.ITEM;

import java.util.List;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.Profile;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiUtils;
import tk.nukeduck.hud.util.Direction.Options;
import tk.nukeduck.hud.util.mode.GlMode;

public final class GlUtil {
	private GlUtil() {}

	private static final double TEXTURE_NORMALIZE = 1.0 / 256.0;

	/** Enables general blending for translucent primitives */
	public static void enableBlendTranslucent() {
		GlStateManager.enableBlendProfile(Profile.PLAYER_SKIN);
	}

	/** Sets the OpenGL color to the 32-bit RGBA composite color */
	public static void color(int color) {
		GlStateManager.color(Colors.red(color), Colors.green(color), Colors.blue(color), Colors.alpha(color));
	}

	/** All axes default to {@code scale}
	 * @see GlStateManager#scale(float, float, float) */
	public static void scale(float scale) {
		GlStateManager.scale(scale, scale, scale);
	}

	/** @see Gui#drawRect(int, int, int, int, int) */
	public static void drawRect(Bounds bounds, int color) {
		Gui.drawRect(bounds.getLeft(), bounds.getTop(), bounds.getRight(), bounds.getBottom(), color);
		GlMode.clean();
	}

	public static void drawBorderRect(Bounds bounds, int color) {
		drawRect(bounds.withWidth(1).grow(0, -1, 0, -1), color);
		drawRect(bounds.withLeft(bounds.getRight() - 1).grow(0, -1, 0, -1), color);

		drawRect(bounds.withHeight(1), color);
		drawRect(bounds.withTop(bounds.getBottom() - 1), color);
	}

	/** @see #drawTexturedModalRect(int, int, int, int, int, int) */
	public static void drawTexturedModalRect(Point position, Bounds texture) {
		drawTexturedModalRect(position.getX(), position.getY(), texture.getX(), texture.getY(), texture.getWidth(), texture.getHeight());
	}

	/** @see #drawTexturedModalRect(int, int, int, int, int, int, int, int) */
	public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
		drawTexturedModalRect(x, y, u, v, Math.abs(width), Math.abs(height), width, height);
	}

	/** @see #drawTexturedModalRect(int, int, int, int, int, int, int, int) */
	public static void drawTexturedModalRect(Bounds bounds, Bounds texture) {
		drawTexturedModalRect(bounds.getX(), bounds.getY(), texture.getX(), texture.getY(), bounds.getWidth(), bounds.getHeight(), texture.getWidth(), texture.getHeight());
	}

	/** Supports negative sized textures
	 * @see net.minecraft.client.gui.Gui#drawTexturedModalRect(int, int, int, int, int, int) */
	public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder builder = tessellator.getBuffer();
		builder.begin(7, DefaultVertexFormats.POSITION_TEX);

		builder.pos(x,         y + height, 0).tex( u                 * TEXTURE_NORMALIZE, (v + textureHeight) * TEXTURE_NORMALIZE).endVertex();
		builder.pos(x + width, y + height, 0).tex((u + textureWidth) * TEXTURE_NORMALIZE, (v + textureHeight) * TEXTURE_NORMALIZE).endVertex();
		builder.pos(x + width, y,          0).tex((u + textureWidth) * TEXTURE_NORMALIZE,  v                  * TEXTURE_NORMALIZE).endVertex();
		builder.pos(x,         y,          0).tex( u                 * TEXTURE_NORMALIZE,  v                  * TEXTURE_NORMALIZE).endVertex();

		tessellator.draw();
	}

	/** Draws text with black borders on all sides */
	public static void drawBorderedString(String text, int x, int y, int color) {
		// Borders
		MC.fontRenderer.drawString(text, x + 1, y, Colors.BLACK, false);
		MC.fontRenderer.drawString(text, x - 1, y, Colors.BLACK, false);
		MC.fontRenderer.drawString(text, x, y + 1, Colors.BLACK, false);
		MC.fontRenderer.drawString(text, x, y - 1, Colors.BLACK, false);

		MC.fontRenderer.drawString(text, x, y, color, false);

		GlMode.clean();
	}

	/** @see #renderSingleItem(ItemStack, int, int) */
	public static void renderSingleItem(ItemStack stack, Point point) {
		renderSingleItem(stack, point.getX(), point.getY());
	}

	/** Renders {@code stack} to the GUI, and reverts lighting side effects
	 *
	 * @see RenderHelper#enableGUIStandardItemLighting()
	 * @see net.minecraft.client.renderer.RenderItem#renderItemAndEffectIntoGUI(ItemStack, int, int)
	 * @see RenderHelper#disableStandardItemLighting() */
	public static void renderSingleItem(ItemStack stack, int x, int y) {
		GlMode.push(ITEM);
		MC.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
		GlMode.pop();
	}

	/** Renders the item with hotbar animations */
	public static void renderHotbarItem(Bounds bounds, ItemStack stack, float partialTicks) {
		if(stack.isEmpty()) return;
		float animationTicks = stack.getAnimationsToGo() - partialTicks;

		GlMode.push(ITEM);
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
		GlMode.pop();
	}

	/** @see GuiUtils#drawHoveringText(ItemStack, List, int, int, int, int, int, net.minecraft.client.gui.FontRenderer) */
	public static void drawTooltipBox(int x, int y, int w, int h) {
		enableBlendTranslucent();
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

		GlStateManager.enableDepth();
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
	 * @see #drawProgressBar(Bounds, float, boolean) */
	public static void drawDamageBar(Bounds bounds, ItemStack stack, boolean vertical) {
		float progress = (float)(stack.getMaxDamage() - stack.getItemDamage()) / stack.getMaxDamage();
		drawProgressBar(bounds, progress, vertical);
	}

	/** Draws a progress bar for item damage
	 * @param progress Index of progress between 0 and 1
	 * @param vertical {@code true} to render bar from bottom to top */
	public static void drawProgressBar(Bounds bounds, float progress, boolean vertical) {
		drawRect(bounds, Colors.BLACK);
		progress = MathHelper.clamp(progress, 0, 1);

		int color = Colors.fromHSV(progress / 3, 1, 1);

		Bounds bar;
		if(vertical) {
			bar = new Bounds(bounds.getWidth() - 1, (int)(progress * bounds.getHeight()));
			bar = bar.anchor(bounds, Direction.SOUTH_WEST);
		} else {
			bar = new Bounds((int)(progress * bounds.getWidth()), bounds.getHeight() - 1);
			bar = bar.anchor(bounds, Direction.NORTH_WEST);
		}
		drawRect(bar, color);
	}

	/** Draws a progress bar with textures
	 * @param progress Index of progress between 0 and 1
	 * @param direction The direction the bar should fill up in */
	public static void drawTexturedProgressBar(Point position, Bounds background, Bounds foreground, float progress, Direction direction) {
		drawTexturedModalRect(position, background);

		Bounds bounds = background.withPosition(position);
		Bounds partialBounds = new Bounds(bounds);
		Bounds partialForeground = new Bounds(foreground);

		if(!Options.VERTICAL.isValid(direction)) {
			int partial = MathHelper.ceil(progress * partialBounds.getWidth());

			partialBounds = partialBounds.withWidth(partial);
			partialForeground = partialForeground.withWidth(partial);
		} else {
			int partial = MathHelper.ceil(progress * partialBounds.getHeight());

			partialBounds = partialBounds.withHeight(partial);
			partialForeground = partialForeground.withHeight(partial);
		}

		Direction anchor = direction.mirror();
		partialBounds = partialBounds.anchor(bounds, anchor);
		partialForeground = partialForeground.anchor(foreground, anchor);

		drawTexturedModalRect(partialBounds.getPosition(), partialForeground);
	}

	/** @return The size of {@code string} as rendered by Minecraft's font renderer */
	public static Point getStringSize(String string) {
		return new Point(MC.fontRenderer.getStringWidth(string), MC.fontRenderer.FONT_HEIGHT);
	}

	/** @param origin The anchor point
	 * @param alignment The alignment around {@code origin}
	 * @see net.minecraft.client.gui.FontRenderer#drawStringWithShadow(String, float, float, int) */
	public static Bounds drawString(String string, Point origin, Direction alignment, int color) {
		Bounds bounds = new Bounds(getStringSize(string)).align(origin, alignment);
		MC.fontRenderer.drawStringWithShadow(string, bounds.getX(), bounds.getY(), color);

		GlMode.clean();
		return bounds;
	}
}
