package tk.nukeduck.hud.util;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.Profile;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

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
}
