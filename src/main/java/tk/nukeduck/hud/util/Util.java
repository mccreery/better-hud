package tk.nukeduck.hud.util;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glEnable;
import static tk.nukeduck.hud.BetterHud.MC;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiUtils;

/** Functions that are used in multiple parts of the mod */
public class Util {
	public static void drawTooltipBox(int x, int y, int w, int h) {
		GlStateManager.enableBlend();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
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
		
		GlStateManager.enableDepth();
	}

	public static void renderQuadWithUV(Tessellator t, int x, int y, float u, float v, int width, int height) {
		renderQuadWithUV(t, x, y, u, v, u + width / 256F, v + height / 256F, width, height);
	}

	public static void renderQuadWithUV(Tessellator t, int x, int y, float u, float v, float u2, float v2, int width, int height) {
		glEnable(GL_BLEND);
		
		BufferBuilder wr = t.getBuffer();
		
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		
		wr.pos(x, y, 0).tex(u, v).endVertex();
		wr.pos(x, y + height, 0).tex(u, v2).endVertex();
		wr.pos(x + width, y + height, 0).tex(u2, v2).endVertex();
		wr.pos(x + width, y, 0).tex(u2, v).endVertex();
		
		t.draw();
	}
	
	/** @see #renderItem(ItemStack, int, int) */
	public static void renderItem(ItemStack stack, Point point) {
		renderItem(stack, point.x, point.y);
	}

	/** Renders an item using the correct lighting */
	public static void renderItem(ItemStack stack, int x, int y) {
		RenderHelper.enableGUIStandardItemLighting();
		MC.getRenderItem().renderItemAndEffectIntoGUI(stack, x + 5, y + 2);
		RenderHelper.disableStandardItemLighting();
	}

	/** Formats {@code x} to {@code n} decimal places */
	public static String formatToPlaces(double x, int n) {
		DecimalFormat format = new DecimalFormat();
		format.setMaximumFractionDigits(n);

		return format.format(x);
	}

	/** @see #join(String, List) */
	public static String join(String delimiter, String... parts) {
		return join(delimiter, Arrays.asList(parts));
	}

	/** @return {@code parts} joined into a single string using {@code delimiter} */
	public static String join(String delimiter, List<String> parts) {
		if(parts.isEmpty()) return "";

		// Estimate size of string builder
		StringBuilder builder = new StringBuilder(parts.get(0).length() * parts.size());

		// Add parts
		for(String part : parts) {
			builder.append(part).append(delimiter);
		}
		builder.setLength(builder.length() - delimiter.length()); // Cut tail
		return builder.toString();
	}

	/** @return {@code true} if the line between {@code min} and {@code max}
	 * overlaps the line between {@code min2} and {@code max2} */
	/*public static boolean lineOverlaps(int min, int max, int min2, int max2) {
		return min  >= min2 && min  < max2
			|| max  >= min2 && max  < max2
			|| min2 >= min  && min2 < max
			|| max2 >= min  && max2 < max;
	}*/

	/** @return The closest value in {@code values} to {@code target} */
	@Deprecated public static int getClosest(int target, Iterable<Integer> values) {
		Iterator<Integer> iterator = values.iterator();

		if(!iterator.hasNext()) {
			throw new IllegalArgumentException("No values");
		}

		int closestValue = iterator.next();
		int closestDistance = Math.abs(target - closestValue);

		while(iterator.hasNext()) {
			int value = iterator.next();
			int distance = Math.abs(target - closestValue);

			if(distance < closestDistance) {
				closestValue = value;
				closestDistance = distance;
			}
		}
		return closestValue;
	}
}
