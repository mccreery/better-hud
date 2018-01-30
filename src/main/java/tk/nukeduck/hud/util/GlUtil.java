package tk.nukeduck.hud.util;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.Profile;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GlUtil {
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
		final int bgColor	 = 0xf7100010;
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
}
