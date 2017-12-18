package tk.nukeduck.hud.util;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import scala.actors.threadpool.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class RenderUtil {
	public static void renderQuad(Tessellator t, int x, int y, int width, int height, float red, float green, float blue, float alpha) {
		glEnable(GL_BLEND);
		glColor4f(red, green, blue, alpha);
		
		glBindTexture(GL_TEXTURE_2D, 0);
		
		t.startDrawingQuads();
		
		t.addVertex(x, y, 0);
		t.addVertex(x, y + height, 0);
		t.addVertex(x + width, y + height, 0);
		t.addVertex(x + width, y, 0);
		
		t.draw();
	}
	
	public static void renderQuadWithUV(Tessellator t, int x, int y, float u, float v, float u2, float v2, int width, int height, float red, float green, float blue, float alpha) {
		glEnable(GL_BLEND);
		glColor4f(red, green, blue, alpha);
		
		t.startDrawingQuads();
		
		t.addVertexWithUV(x, y, 0, u, v);
		t.addVertexWithUV(x, y + height, 0, u, v2);
		t.addVertexWithUV(x + width, y + height, 0, u2, v2);
		t.addVertexWithUV(x + width, y, 0, u2, v);
		
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
	}
	
	public static void renderItem(RenderItem ri, FontRenderer fr, TextureManager tm, ItemStack item, int x, int y) {
		if(item.hasEffect()) {
			ri.renderItemIntoGUI(fr, tm, item, x, y, true);
		} else ri.renderItemIntoGUI(fr, tm, item, x, y);
		glColor4d(1.0, 1.0, 1.0, 1.0);
		glDisable(GL_LIGHTING);
	}
	
	public static void renderItem(RenderItem ri, FontRenderer fr, TextureManager tm, ItemStack item, int x, int y, double alpha) {
		ri.renderWithColor = false;
		glEnable(GL_BLEND);
		glColor4d(1.0, 1.0, 1.0, alpha);
		if(item.hasEffect()) ri.renderItemIntoGUI(fr, tm, item, x, y, true);
		else ri.renderItemIntoGUI(fr, tm, item, x, y);
		glColor4d(1.0, 1.0, 1.0, 1.0);
		glDisable(GL_LIGHTING);
		ri.renderWithColor = true;
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