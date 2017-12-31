package tk.nukeduck.hud.util;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.glColor4d;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiUtils;

public class RenderUtil {
	public static void drawTooltipBox(int x, int y, int w, int h) {
		GlStateManager.enableBlend();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();

		final int zLevel      = 300;
		final int bgColor     = 0xf7100010 /*0xF0100010*/;
		final int borderStart = 0x505000ff;
		final int borderEnd   = (borderStart & 0xfefefe) >> 1 | borderStart & 0xff000000;

		// Box
		GuiUtils.drawGradientRect(zLevel, x+1, y,     x+w-1, y+1,   bgColor, bgColor); // Top
		GuiUtils.drawGradientRect(zLevel, x,   y+1,   x+w,   y+h-1, bgColor, bgColor); // Middle
		GuiUtils.drawGradientRect(zLevel, x+1, y+h-1, x+w-1, y+h,   bgColor, bgColor); // Bottom

		// Borders
		GuiUtils.drawGradientRect(zLevel, x+1,   y+1,   x+w-1, y+2,   borderStart, borderStart); // Top
		GuiUtils.drawGradientRect(zLevel, x+1,   y+2,   x+2,   y+h-2, borderStart, borderEnd);   // Left
		GuiUtils.drawGradientRect(zLevel, x+w-2, y+2,   x+w-1, y+h-2, borderStart, borderEnd);   // Right
		GuiUtils.drawGradientRect(zLevel, x+1,   y+h-2, x+w-1, y+h-1, borderEnd,   borderEnd);   // Bottom
		
		GlStateManager.enableDepth();
	}

	/**
	 * Draws a solid color rectangle with the specified coordinates and color (ARGB format). Args: x1, y1, x2, y2, color
	 */
	/*public static void drawRect(int left, int top, int right, int bottom, int color) {
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		GlStateManager.disableTexture2D();
		GlStateManager.disableAlpha();
		GlStateManager.disableBlend();

		float f3 = (float)(color >> 24 & 0xff) / 255.0F;
		float f = (float)(color >> 16 & 0xff) / 255.0F;
		float f1 = (float)(color >> 8 & 0xff) / 255.0F;
		float f2 = (float)(color & 0xff) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer worldrenderer = tessellator.getBuffer();
		//GlStateManager.enableBlend();
		GL11.glEnable(GL11.GL_BLEND);
		//GlStateManager.disableTexture2D();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		//GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GL11.glColor4f(f, f1, f2, f3);
		worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos((double)left, (double)bottom, 0.0D).endVertex();
		worldrenderer.pos((double)right, (double)bottom, 0.0D).endVertex();
		worldrenderer.pos((double)right, (double)top, 0.0D).endVertex();
		worldrenderer.pos((double)left, (double)top, 0.0D).endVertex();
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		//GlStateManager.enableTexture2D();
		//GL11.glDisable(GL11.GL_BLEND);
		//GlStateManager.disableBlend();

		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableTexture2D();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
	}*/

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

	public static void renderItem(RenderItem ri, FontRenderer fr, TextureManager tm, ItemStack item, int x, int y) {
		glColor4d(1.0, 1.0, 1.0, 1.0);
		glDisable(GL_LIGHTING);
		if(item.hasEffect()) {
			ri.renderItemAndEffectIntoGUI(item, x, y);
		} else ri.renderItemIntoGUI(item, x, y);
	}

	@Deprecated
	public static Bounds renderStrings(FontRenderer fr, ArrayList<String> text, int x, int y, int color, Direction pos) {
		return renderStrings(fr, text.toArray(new String[text.size()]), x, y, color, pos);
	}
	@Deprecated
	public static Bounds renderStrings(FontRenderer fr, String[] text, int x, int y, int color, Direction pos) {
		boolean right = pos == Direction.NORTH_EAST || pos == Direction.EAST || pos == Direction.SOUTH_EAST;
		boolean bottom = pos == Direction.SOUTH_WEST || pos == Direction.SOUTH || pos == Direction.SOUTH_EAST;
		
		int maxWidth = 0;
		for(int i = 0; i < text.length; i++) {
			int width = fr.getStringWidth(text[i]);
			fr.drawStringWithShadow(text[i], right ? x - width : x, bottom ? y - ((i + 1) * (fr.FONT_HEIGHT + 2) - 2) : y + (i * (fr.FONT_HEIGHT + 2)), color);
			if(width > maxWidth) maxWidth = width;
		}
		
		int height = text.length * (fr.FONT_HEIGHT + 2) - 2;
		int bx = right ? x - maxWidth : x;
		int by = bottom ? y - height : y;
		
		return new Bounds(bx, by, maxWidth, height);
	}
}
