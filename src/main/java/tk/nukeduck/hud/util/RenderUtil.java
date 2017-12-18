package tk.nukeduck.hud.util;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ENABLE_BIT;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glColor4d;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;

public class RenderUtil {
	public static int colorRGB(int r, int g, int b) {
		return colorARGB(255, r, g, b);
	}
	
	public static int colorARGB(int a, int r, int g, int b) {
		return (a & 0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
	}
	
	public static void renderQuad(Tessellator t, int x, int y, int width, int height, float red, float green, float blue, float alpha) {
		glPushAttrib(GL_ENABLE_BIT);
		glEnable(GL_BLEND);
		glDisable(GL_TEXTURE_2D);
		glColor4f(red, green, blue, alpha);

		VertexBuffer wr = t.getBuffer();
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		wr.pos(x, y, 0).endVertex();
		wr.pos(x, y + height, 0).endVertex();
		wr.pos(x + width, y + height, 0).endVertex();
		wr.pos(x + width, y, 0).endVertex();
		t.draw();
		glPopAttrib();
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
        VertexBuffer worldrenderer = tessellator.getBuffer();
        //GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_BLEND);
        //GlStateManager.disableTexture2D();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        //GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glColor4f(f, f1, f2, f3);
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos((double)left, (double)bottom, 0.0D).endVertex();
        worldrenderer.pos((double)right, (double)bottom, 0.0D).endVertex();
        worldrenderer.pos((double)right, (double)top, 0.0D).endVertex();
        worldrenderer.pos((double)left, (double)top, 0.0D).endVertex();
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        //GlStateManager.enableTexture2D();
        //GL11.glDisable(GL11.GL_BLEND);
        //GlStateManager.disableBlend();
    }
    
    public static void drawProgressBar(int x, int y, int right, int bottom, float value) {
    	int width = right - x - 1;
    	int color = colorRGB(Math.round((1.0f - value) * 255), Math.round(value * 255), 0);
    	drawRect(x, y, right, bottom, RenderUtil.colorRGB(0, 0, 0));
    	drawRect(x, y, x + Math.round(value * width), bottom - 1, color);
    }
    
    public static void drawProgressBarV(int x, int y, int right, int bottom, float value) {
    	int height = bottom - y - 1;
    	int color = colorRGB(Math.round((1.0f - value) * 255), Math.round(value * 255), 0);
    	drawRect(x, y, right, bottom, RenderUtil.colorRGB(0, 0, 0));
    	drawRect(x, bottom - 1 - Math.round(value * height), right - 1, bottom - 1, color);
    }
	
    public static void renderQuadWithUV(Tessellator t, int x, int y, float u, float v, int width, int height) {
    	renderQuadWithUV(t, x, y, u, v, u + width / 256F, v + height / 256F, width, height);
    }
    
	public static void renderQuadWithUV(Tessellator t, int x, int y, float u, float v, float u2, float v2, int width, int height) {
		glEnable(GL_BLEND);
		
		VertexBuffer wr = t.getBuffer();
		
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
	
	/*public static void renderItemAlpha(RenderItem ri, FontRenderer fr, TextureManager tm, ItemStack item, int x, int y, double alpha) {
		glEnable(GL_BLEND);
		glColor4d(1.0, 1.0, 1.0, alpha);
		
		IBakedModel iBakedModel = ri.getItemModelMesher().getItemModel(item);
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
	}*/
	
	/*public static void renderStrings(FontRenderer fr, ArrayList<String> text, int x, int y, int color) {
		renderStrings(fr, text, x, y, color, Position.TOP_LEFT);
	}*/
	
	/*public static void renderStrings(FontRenderer fr, int x, int y, int color, Posit, String... text) {
		ArrayList<String> a = new ArrayList<String> (Arrays.asList(text));
		renderStrings(fr, a, x, y, color, right);
	}*/
	
	public static Bounds renderStrings(FontRenderer fr, ArrayList<String> text, int x, int y, int color, Position pos) {
		return renderStrings(fr, text.toArray(new String[text.size()]), x, y, color, pos);
	}
	public static Bounds renderStrings(FontRenderer fr, String[] text, int x, int y, int color, Position pos) {
		boolean right = pos == Position.TOP_RIGHT || pos == Position.MIDDLE_RIGHT || pos == Position.BOTTOM_RIGHT;
		boolean bottom = pos == Position.BOTTOM_LEFT || pos == Position.BOTTOM_CENTER || pos == Position.BOTTOM_RIGHT;
		
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
	
	public static Bounds renderStrings(FontRenderer fr, ArrayList<ColoredText> text, int x, int y, Position pos) {
		return renderStrings(fr, text.toArray(new ColoredText[text.size()]), x, y, pos);
	}
	public static Bounds renderStrings(FontRenderer fr, ColoredText[] text, int x, int y, Position pos) {
		boolean right = pos == Position.TOP_RIGHT || pos == Position.MIDDLE_RIGHT || pos == Position.BOTTOM_RIGHT;
		boolean bottom = pos == Position.BOTTOM_LEFT || pos == Position.BOTTOM_CENTER || pos == Position.BOTTOM_RIGHT;
		
		int maxWidth = 0;
		for(int i = 0; i < text.length; i++) {
			int width = fr.getStringWidth(text[i].text);
			fr.drawStringWithShadow(text[i].text, right ? x - width : x, bottom ? y - ((i + 1) * (fr.FONT_HEIGHT + 2) - 2) : y + (i * (fr.FONT_HEIGHT + 2)), text[i].color);
			if(width > maxWidth) maxWidth = width;
		}
		
		int height = text.length * (fr.FONT_HEIGHT + 2) - 2;
		int bx = right ? x - maxWidth : x;
		int by = bottom ? y - height : y;
		
		return new Bounds(bx, by, maxWidth, height);
	}
	
	public static void zIncrease() {
		glTranslatef(0.0F, 0.0F, -0.001F);
	}
	
    /*public static void drawHoveringText(List textLines, int x, int y, ScaledResolution size) {
    	FontRenderer font = BetterHud.mc.fontRendererObj;
        if (!textLines.isEmpty())
        {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int k = 0;
            Iterator iterator = textLines.iterator();

            while (iterator.hasNext())
            {
                String s = (String)iterator.next();
                int l = font.getStringWidth(s);

                if (l > k)
                {
                    k = l;
                }
            }

            int j2 = x + 12;
            int k2 = y - 12;
            int i1 = 8;

            if (textLines.size() > 1)
            {
                i1 += 2 + (textLines.size() - 1) * 10;
            }

            if (j2 + k > size.getScaledWidth())
            {
                j2 -= 28 + k;
            }

            if (k2 + i1 + 6 > size.getScaledHeight())
            {
                k2 = size.getScaledHeight() - i1 - 6;
            }
            
            int j1 = -267386864;
            drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
            drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
            drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
            drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
            drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
            int k1 = 1347420415;
            int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
            drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
            drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
            drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
            drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

            for (int i2 = 0; i2 < textLines.size(); ++i2)
            {
                String s1 = (String)textLines.get(i2);
                font.drawStringWithShadow(s1, j2, k2, -1);

                if (i2 == 0)
                {
                    k2 += 2;
                }

                k2 += 10;
            }
            
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }*/
    
    /**
     * Draws a rectangle with a vertical gradient between the specified colors (ARGB format). Args : x1, y1, x2, y2,
     * topColor, bottomColor
     */
    /*public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
    {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.startDrawingQuads();
        worldrenderer.setColorRGBA_F(f1, f2, f3, f);
        worldrenderer.addVertex((double)right, (double)top, 0.0F);
        worldrenderer.addVertex((double)left, (double)top, 0.0F);
        worldrenderer.setColorRGBA_F(f5, f6, f7, f4);
        worldrenderer.addVertex((double)left, (double)bottom, 0.0F);
        worldrenderer.addVertex((double)right, (double)bottom, 0.0F);
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }*/
}