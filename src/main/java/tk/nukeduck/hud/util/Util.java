package tk.nukeduck.hud.util;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static tk.nukeduck.hud.BetterHud.MC;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
		final int bgColor	 = 0xf7100010 /*0xF0100010*/;
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
	
	public static void billBoard(Entity entity, EntityPlayer player, float partialTicks) {
		glDisable(GL_DEPTH_TEST);
		
		glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		double dx = (entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks) - (player.prevPosX + (player.posX - player.prevPosX) * partialTicks); 
		double dy = (entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks) - (player.prevPosY + (player.posY - player.prevPosY) * partialTicks); 
		double dz = (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks) - (player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks);
		
		glDisable(GL_LIGHTING);
		
		double scale = Math.max(1, Math.sqrt(dx * dx + dy * dy + dz * dz) / 5);
		
		glTranslated(dx, dy + 0.5F + entity.height, dz);
		GL11.glScaled(scale, scale, scale);
		glRotatef(-player.rotationYaw, 0.0F, 1.0F, 0.0F);
		glRotatef(player.rotationPitch, 1.0F, 0.0F, 0.0F);
		glRotatef(180, 0, 0, 1);
		glTranslatef(-0.5F, -0.5F, 0.0F);
		
		glEnable(GL_BLEND);
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
}
