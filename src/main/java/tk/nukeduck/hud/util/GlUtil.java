package tk.nukeduck.hud.util;

import static tk.nukeduck.hud.BetterHud.ICONS;
import static tk.nukeduck.hud.BetterHud.MC;

import java.util.List;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.Profile;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GlUtil {
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

	/** @see #renderSingleItem(ItemStack, int, int) */
	public static void renderSingleItem(ItemStack stack, Point point) {
		renderSingleItem(stack, point.x, point.y);
	}

	/** Renders {@code stack} to the GUI, and reverts lighting side effects
	 *
	 * @see RenderHelper#enableGUIStandardItemLighting()
	 * @see net.minecraft.client.renderer.RenderItem#renderItemAndEffectIntoGUI(ItemStack, int, int)
	 * @see RenderHelper#disableStandardItemLighting() */
	public static void renderSingleItem(ItemStack stack, int x, int y) {
		RenderHelper.enableGUIStandardItemLighting();
		MC.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
		RenderHelper.disableStandardItemLighting();
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

	/** Draws text with black borders on all sides */
	public static void drawBorderedString(String text, int x, int y, int color) {
		// Borders
		MC.fontRenderer.drawString(text, x + 1, y, Colors.BLACK, false);
		MC.fontRenderer.drawString(text, x - 1, y, Colors.BLACK, false);
		MC.fontRenderer.drawString(text, x, y + 1, Colors.BLACK, false);
		MC.fontRenderer.drawString(text, x, y - 1, Colors.BLACK, false);
	
		MC.fontRenderer.drawString(text, x, y, color, false);
	}

	/** @see #drawTexturedModalRect(int, int, int, int, int, int) */
	public static void drawTexturedModalRect(Point position, Bounds texture) {
		drawTexturedModalRect(position.x, position.y, texture.x(), texture.y(), texture.width(), texture.height());
	}

	/** @see net.minecraft.client.gui.Gui#drawTexturedModalRect(int, int, int, int, int, int) */
	public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder builder = tessellator.getBuffer();
		builder.begin(7, DefaultVertexFormats.POSITION_TEX);

		builder.pos(x,         y + height, 0).tex( u          * TEXTURE_NORMALIZE, (v + height) * TEXTURE_NORMALIZE).endVertex();
		builder.pos(x + width, y + height, 0).tex((u + width) * TEXTURE_NORMALIZE, (v + height) * TEXTURE_NORMALIZE).endVertex();
		builder.pos(x + width, y,          0).tex((u + width) * TEXTURE_NORMALIZE,  v           * TEXTURE_NORMALIZE).endVertex();
		builder.pos(x,         y,          0).tex( u          * TEXTURE_NORMALIZE,  v           * TEXTURE_NORMALIZE).endVertex();

		tessellator.draw();
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

	/** Renders a multi-row health bar with {@code health} full hearts
	 * and {@code maxHealth} total hearts
	 *
	 * @param background The texture coordinates for the background icon
	 * @param half The texture coordinates for the half unit icon
	 * @param full The texture coordinates for a full unit icon */
	public static void renderBar(int current, int max, Point position, Bounds background, Bounds half, Bounds full) {
		Point icon = new Point(position);
	
		color(Colors.WHITE);
		for(int i = 0; i < max; icon.x = position.x, icon.y += 9) {
			for(int j = 0; j < 20 && i < max; i += 2, j += 2, icon.x += 8) {
				if(background != null) {
					drawTexturedModalRect(icon, background);
				}
	
				if(i + 1 < current) {
					drawTexturedModalRect(icon, full);
				} else if(i < current) {
					drawTexturedModalRect(icon, half);
				}
			}
		}
	}

	/** @see renderBar */
	public static void renderHealthBar(int health, int maxHealth, Point position) {
		MC.getTextureManager().bindTexture(ICONS);
	
		renderBar(health, maxHealth, position,
			new Bounds(16, 0, 9, 9), new Bounds(61, 0, 9, 9), new Bounds(52, 0, 9, 9));
	}
}
