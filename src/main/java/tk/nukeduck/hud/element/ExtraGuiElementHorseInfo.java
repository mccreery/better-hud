package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityHorse;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.RenderUtil;

public class ExtraGuiElementHorseInfo extends ExtraGuiElement {
	public ExtraGuiElementHorseInfo() {
		name = "horseInfo";
		modes = new String[] {"horse.jump", "horse.speed", "both"};
		defaultMode = 2;
	}
	
	public void renderInfo(EntityLivingBase entity, Minecraft mc, float partialTicks) {
		if(enabled && entity instanceof EntityHorse) {
			Tessellator t = Tessellator.getInstance();
			
			glPushMatrix(); {
				ArrayList<String> infoParts = new ArrayList<String>();
				
				EntityHorse horse = (EntityHorse) entity;
				
				String mode = currentMode();
				if(!mode.equals("horse.speed")) {
					infoParts.add(FormatUtil.translatePre("strings.jump", String.valueOf(Math.round(getJumpHeight(horse) * 1000.0) / 1000.0)));
				}
				if(!mode.equals("horse.jump")) {
					infoParts.add(FormatUtil.translatePre("strings.speed", String.valueOf(Math.round(horse.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue() * 43.05 * 100.0) / 100.0)));
				}
				
				int horseWidth = FormatUtil.getLongestWidth(BetterHud.fr, infoParts) + 10;
				int horseHeight = infoParts.size() * (BetterHud.fr.FONT_HEIGHT + 2) + 8;
				
				RenderUtil.billBoard(entity, mc.thePlayer, partialTicks);
				
				float scale = 1.0F / horseWidth;
				glScalef(scale, scale, scale);
				
				// Rendering starts
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
				glTranslatef(0.0F, -horseHeight - 5, 0.0F);
				
				RenderUtil.renderQuad(t, 0, 0, horseWidth, horseHeight, 0, 0, 0, 0.5F);
				RenderUtil.zIncrease();
				for(int i = 0; i < infoParts.size(); i++) {
					mc.ingameGUI.drawString(BetterHud.fr, infoParts.get(i), 5, 5 + (BetterHud.fr.FONT_HEIGHT + 2) * i, 0xffffff);
				}
			}
			glPopMatrix();
		}
	}
	
	public double getJumpHeight(EntityHorse horse) {
		double yVelocity = horse.getHorseJumpStrength(); //horses's jump strength attribute
		double jumpHeight = 0;
		while (yVelocity > 0) {
			jumpHeight += yVelocity;
			yVelocity -= 0.08;
			yVelocity *= 0.98;
		}
		return jumpHeight;
	}
}