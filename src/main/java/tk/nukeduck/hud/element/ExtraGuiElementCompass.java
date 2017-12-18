package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;

public class ExtraGuiElementCompass extends ExtraGuiElement {
	public static int nColor = 16711680;	// 256*256*255 + 0 + 0
	public static int ewColor = 16777215;	// 256*256*255 + 256*255 + 255
	public static int sColor = 255;			// 256*256*0 + 256*0 + 255
	
	public ExtraGuiElementCompass() {
		name = "compass";
		modes = new String[] {"simple", "fancy", "fancier"};
		defaultMode = 2;
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		int fanciness = mode;
		
		float transform = mc.thePlayer.rotationYaw / 115; //115 ~= (360 / Math.PI)
		int nTransparency = (int) Math.abs(Math.sin(transform) * 255);
		int wTransparency = (int) Math.abs(Math.sin(transform + 0.7826) * 255); //90 / 115
		int sTransparency = (int) Math.abs(Math.sin(transform + 1.5652) * 255); //180 / 115
		int eTransparency = (int) Math.abs(Math.sin(transform + 2.3478) * 255);  //270 / 115
		
		mc.ingameGUI.drawRect(halfWidth - 90, 18, halfWidth + 90, 30, 0xaa000000);
		if(fanciness > 0) {
			mc.ingameGUI.drawRect(halfWidth - 40, 18, halfWidth + 40, 30, 0x55555555);
		}
		
		glEnable(GL_BLEND);
		
		//57.296 = ~180 / Math.PI
		if(nTransparency > 10) {
			glPushMatrix(); {
				glTranslatef(halfWidth - (int) (Math.sin((mc.thePlayer.rotationYaw + 180) / 57.296) * 100), 20, 0.0F);
				if(fanciness > 0) {
					float size = (float) nTransparency / 128F;
					glScalef(size, size, 1.0F);
				}
				mc.ingameGUI.drawCenteredString(fr, "N", 0, 0, 16777216*nTransparency + nColor); // 16777216 = 256^3
			}
			glPopMatrix();
		}
		if(eTransparency > 10) {
			glPushMatrix(); {
				glTranslatef(halfWidth - (int) (Math.sin((mc.thePlayer.rotationYaw + 90) / 57.296) * 100), 20, 0.0F);
				if(fanciness > 0) {
					float size = (float) eTransparency / 128F;
					glScalef(size, size, 1.0F);
				}
				mc.ingameGUI.drawCenteredString(fr, "E", 0, 0, 16777216*eTransparency + ewColor); // 16777216 = 256^3
			}
			glPopMatrix();
		}
		if(sTransparency > 10) {
			glPushMatrix(); {
				glTranslatef(halfWidth - (int) (Math.sin((mc.thePlayer.rotationYaw + 360) / 57.296) * 100), 20, 0.0F);
				if(fanciness > 0) {
					float size = (float) sTransparency / 128F;
					glScalef(size, size, 1.0F);
				}
				mc.ingameGUI.drawCenteredString(fr, "S", 0, 0, 16777216*sTransparency + sColor); // 16777216 = 256^3
			}
			glPopMatrix();
		}
		if(wTransparency > 10) {
			glPushMatrix(); {
				glTranslatef(halfWidth - (int) (Math.sin((mc.thePlayer.rotationYaw + 270) / 57.296) * 100), 20, 0.0F);
				if(fanciness > 0) {
					float size = (float) wTransparency / 128F;
					glScalef(size, size, 1.0F);
				}
				mc.ingameGUI.drawCenteredString(fr, "W", 0, 0, 16777216*wTransparency + ewColor); // 16777216 = 256^3
			}
			glPopMatrix();
		}
		
		// TODO Add NW, NE, SW, SE to "Fancier" and NNW, NWW, NNE, NEE, SSW, SWW, SSE, SEE to new "Fanciest"
		
		if(fanciness > 0) {
			mc.ingameGUI.drawRect(halfWidth - 1, 15, halfWidth + 1, 22, 0xffff0000);
			mc.ingameGUI.drawRect(halfWidth - 90, 15, halfWidth - 89, 22, 0xffff0000);
			mc.ingameGUI.drawRect(halfWidth + 90, 15, halfWidth + 89, 22, 0xffff0000);
		} else {
			mc.ingameGUI.drawRect(halfWidth - 1, 15, halfWidth + 1, 22, 0xffffffff);
			mc.ingameGUI.drawRect(halfWidth - 90, 15, halfWidth - 89, 22, 0xffffffff);
			mc.ingameGUI.drawRect(halfWidth + 90, 15, halfWidth + 89, 22, 0xffffffff);
		}
		
		// TODO Optimise this
		if(fanciness > 1) {
			for(double i = 0.1; i < 0.9; i += 0.1) {
				int loc = (int) (Math.asin(i) / Math.PI * 180);
				mc.ingameGUI.drawRect(halfWidth + loc - 91, 16, halfWidth + loc - 90, 22, 0xffffffff);
				mc.ingameGUI.drawRect(halfWidth - loc + 91, 16, halfWidth - loc + 90, 22, 0xffffffff);
			}
		}
	}
}
