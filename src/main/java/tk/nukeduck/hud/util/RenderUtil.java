package tk.nukeduck.hud.util;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import scala.actors.threadpool.Arrays;
import tk.nukeduck.hud.BetterHud;

public class RenderUtil {
	public static void renderQuad(Tessellator t, int x, int y, int width, int height, float red, float green, float blue, float alpha) {
		glEnable(GL_BLEND);
		glColor4f(red, green, blue, alpha);
		
		glBindTexture(GL_TEXTURE_2D, 0);
		
		WorldRenderer wr = t.getWorldRenderer();
		
		wr.startDrawingQuads();
		
		wr.addVertex(x, y, 0);
		wr.addVertex(x, y + height, 0);
		wr.addVertex(x + width, y + height, 0);
		wr.addVertex(x + width, y, 0);
		
		t.draw();
	}
	
	// Tell me, Mojang, why do your blending functions mess everything ever up?
    /**
     * Draws a solid color rectangle with the specified coordinates and color (ARGB format). Args: x1, y1, x2, y2, color
     */
    public static void drawRect(int left, int top, int right, int bottom, int color) {
        int j1;
        
        if (left < right) {
            j1 = left;
            left = right;
            right = j1;
        }
        if (top < bottom) {
            j1 = top;
            top = bottom;
            bottom = j1;
        }
        
        float f3 = (float)(color >> 24 & 0xff) / 255.0F;
        float f = (float)(color >> 16 & 0xff) / 255.0F;
        float f1 = (float)(color >> 8 & 0xff) / 255.0F;
        float f2 = (float)(color & 0xff) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        //GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_BLEND);
        //GlStateManager.disableTexture2D();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        //GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glColor4f(f, f1, f2, f3);
        worldrenderer.startDrawingQuads();
        worldrenderer.addVertex((double)left, (double)bottom, 0.0D);
        worldrenderer.addVertex((double)right, (double)bottom, 0.0D);
        worldrenderer.addVertex((double)right, (double)top, 0.0D);
        worldrenderer.addVertex((double)left, (double)top, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        //GlStateManager.enableTexture2D();
        //GL11.glDisable(GL11.GL_BLEND);
        //GlStateManager.disableBlend();
    }
	
	public static void renderQuadWithUV(Tessellator t, int x, int y, float u, float v, float u2, float v2, int width, int height, float red, float green, float blue, float alpha) {
		glEnable(GL_BLEND);
		glColor4f(red, green, blue, alpha);
		
		WorldRenderer wr = t.getWorldRenderer();
		
		wr.startDrawingQuads();
		
		wr.addVertexWithUV(x, y, 0, u, v);
		wr.addVertexWithUV(x, y + height, 0, u, v2);
		wr.addVertexWithUV(x + width, y + height, 0, u2, v2);
		wr.addVertexWithUV(x + width, y, 0, u2, v);
		
		t.draw();
	}
	
	public static void billBoard(Entity entity, EntityPlayer player, float partialTicks) {
		glDisable(GL_DEPTH_TEST);
		
		glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		double dx = (entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks) - (player.prevPosX + (player.posX - player.prevPosX) * partialTicks); 
        double dy = (entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks) - (player.prevPosY + (player.posY - player.prevPosY) * partialTicks); 
        double dz = (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks) - (player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks);
		
        glDisable(GL_LIGHTING);
        
    	glTranslated(dx, dy + 0.5F + entity.height, dz);
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
	
	public static void renderItemAlpha(RenderItem ri, FontRenderer fr, TextureManager tm, ItemStack item, int x, int y, double alpha) {
		glEnable(GL_BLEND);
		glColor4d(1.0, 1.0, 1.0, alpha);
		
		IBakedModel iBakedModel = BetterHud.ri.getItemModelMesher().getItemModel(item);
		TextureAtlasSprite textureAtlasSprite = BetterHud.mc.getTextureMapBlocks().getAtlasSprite(iBakedModel.getTexture().getIconName());
		BetterHud.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.startDrawingQuads();
		worldrenderer.addVertexWithUV((double)(x),          (double)(y + 16),  0.0, (double)textureAtlasSprite.getMinU(), (double)textureAtlasSprite.getMaxV());
		worldrenderer.addVertexWithUV((double)(x + 16),  (double)(y + 16),  0.0, (double)textureAtlasSprite.getMaxU(), (double)textureAtlasSprite.getMaxV());
		worldrenderer.addVertexWithUV((double)(x + 16),  (double)(y),           0.0, (double)textureAtlasSprite.getMaxU(), (double)textureAtlasSprite.getMinV());
		worldrenderer.addVertexWithUV((double)(x),          (double)(y),           0.0, (double)textureAtlasSprite.getMinU(), (double)textureAtlasSprite.getMinV());
		tessellator.draw();
	}
	
	public static void renderStrings(FontRenderer fr, ArrayList<String> text, int x, int y, int color) {
		renderStrings(fr, text, x, y, color, false);
	}
	
	public static void renderStrings(FontRenderer fr, int x, int y, int color, boolean right, String... text) {
		ArrayList<String> a = new ArrayList<String> (Arrays.asList(text));
		renderStrings(fr, a, x, y, color, right);
	}
	
	public static void renderStrings(FontRenderer fr, ArrayList<String> text, int x, int y, int color, boolean right) {
		if(right) {
			for(int i = 0; i < text.size(); i++) {
				fr.drawStringWithShadow(text.get(i), x - fr.getStringWidth(text.get(i)), y + (i * (fr.FONT_HEIGHT + 2)), 0xffffff);
			}
		} else {
			for(int i = 0; i < text.size(); i++) {
				fr.drawStringWithShadow(text.get(i), x, y + (i * (fr.FONT_HEIGHT + 2)), 0xffffff);
			}
		}
	}
	
	public static void zIncrease() {
		glTranslatef(0.0F, 0.0F, -0.001F);
	}
}