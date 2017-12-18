package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import tk.nukeduck.hud.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;

public class ExtraGuiElementCompass extends ExtraGuiElement {
	public static final double degreesPerRadian = 180.0 / Math.PI;
	
	public static int nColor = 0xff0000;
	public static int ewColor = 0xffffff;
	public static int sColor = 0x0000ff;
	
	public ExtraGuiElementCompass() {
		name = "compass";
		modes = new String[] {"simple", "fancy", "fancier"};
		defaultMode = 2;
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		int fanciness = mode;
		
		double offsetQuarter = 90 / (degreesPerRadian * 2);
		double transform = mc.thePlayer.rotationYaw / (degreesPerRadian * 2);
		
		// Come on Java, gimme unsigned bytes already
		short nTransparency = (short) Math.abs(Math.sin(transform) * 255);
		short wTransparency = (short) Math.abs(Math.sin(transform + offsetQuarter) * 255);
		short sTransparency = (short) Math.abs(Math.sin(transform + offsetQuarter * 2) * 255);
		short eTransparency = (short) Math.abs(Math.sin(transform - offsetQuarter) * 255);
		
		RenderUtil.drawRect(halfWidth - 90, 18, halfWidth + 90, 30, 0xaa000000);
		if(fanciness > 0) {
			RenderUtil.drawRect(halfWidth - 40, 18, halfWidth + 40, 30, 0x55555555);
		}
		
		glEnable(GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		if(nTransparency > 10) {
			glPushMatrix(); {
				glTranslatef(halfWidth - (int) (Math.sin((mc.thePlayer.rotationYaw + 180) / degreesPerRadian) * 100), 20, 0.0F);
				if(fanciness > 0) {
					float size = (float) nTransparency / 128F;
					glScalef(size, size, 1.0F);
				}
				mc.ingameGUI.drawCenteredString(fr, "N", 0, 0, nTransparency << 24 | nColor);
			}
			glPopMatrix();
		}
		if(eTransparency > 10) {
			glPushMatrix(); {
				glTranslatef(halfWidth - (int) (Math.sin((mc.thePlayer.rotationYaw + 90) / degreesPerRadian) * 100), 20, 0.0F);
				if(fanciness > 0) {
					float size = (float) eTransparency / 128F;
					glScalef(size, size, 1.0F);
				}
				mc.ingameGUI.drawCenteredString(fr, "E", 0, 0, eTransparency << 24 | ewColor);
			}
			glPopMatrix();
		}
		if(sTransparency > 10) {
			glPushMatrix(); {
				glTranslatef(halfWidth - (int) (Math.sin((mc.thePlayer.rotationYaw) / degreesPerRadian) * 100), 20, 0.0F);
				if(fanciness > 0) {
					float size = (float) sTransparency / 128F;
					glScalef(size, size, 1.0F);
				}
				mc.ingameGUI.drawCenteredString(fr, "S", 0, 0, sTransparency << 24 | sColor);
			}
			glPopMatrix();
		}
		if(wTransparency > 10) {
			glPushMatrix(); {
				glTranslatef(halfWidth - (int) (Math.sin((mc.thePlayer.rotationYaw - 90) / degreesPerRadian) * 100), 20, 0.0F);
				if(fanciness > 0) {
					float size = (float) wTransparency / 128F;
					glScalef(size, size, 1.0F);
				}
				mc.ingameGUI.drawCenteredString(fr, "W", 0, 0, wTransparency << 24 | ewColor); // 16777216 = 256^3
			}
			glPopMatrix();
		}
		
		// TODO Add NW, NE, SW, SE to "Fancier" and NNW, NWW, NNE, NEE, SSW, SWW, SSE, SEE to new "Fanciest"
		
		if(fanciness > 0) {
			RenderUtil.drawRect(halfWidth - 1, 15, halfWidth + 1, 22, 0xffff0000);
			RenderUtil.drawRect(halfWidth - 90, 15, halfWidth - 89, 22, 0xffff0000);
			RenderUtil.drawRect(halfWidth + 90, 15, halfWidth + 89, 22, 0xffff0000);
		} else {
			RenderUtil.drawRect(halfWidth - 1, 15, halfWidth + 1, 22, 0xffffffff);
			RenderUtil.drawRect(halfWidth - 90, 15, halfWidth - 89, 22, 0xffffffff);
			RenderUtil.drawRect(halfWidth + 90, 15, halfWidth + 89, 22, 0xffffffff);
		}
		
		// TODO Optimise this
		if(fanciness > 1) {
			for(double i = 0.1; i < 0.9; i += 0.1) {
				int loc = (int) (Math.asin(i) / Math.PI * 180);
				RenderUtil.drawRect(halfWidth + loc - 91, 16, halfWidth + loc - 90, 22, 0xffffffff);
				RenderUtil.drawRect(halfWidth - loc + 91, 16, halfWidth - loc + 90, 22, 0xffffffff);
			}
		}
	}
}
