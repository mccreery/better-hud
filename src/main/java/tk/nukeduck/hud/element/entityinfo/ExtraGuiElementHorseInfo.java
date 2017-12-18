package tk.nukeduck.hud.element.entityinfo;

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
import tk.nukeduck.hud.element.settings.ElementSettingBoolean;
import tk.nukeduck.hud.element.settings.ElementSettingSlider;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.RenderUtil;

public class ExtraGuiElementHorseInfo extends ExtraGuiElementEntityInfo {
	ElementSettingBoolean jump;
	ElementSettingBoolean speed;
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		jump.value = true;
		speed.value = true;
		distance.value = 100;
	}
	
	@Override
	public String getName() {
		return "horseInfo";
	}
	
	public ExtraGuiElementHorseInfo() {
		//modes = new String[] {"horse.jump", "horse.speed", "both"};
		//defaultMode = 2;
		this.settings.add(jump = new ElementSettingBoolean("jump"));
		this.settings.add(speed = new ElementSettingBoolean("speed"));
		this.settings.add(distance = new ElementSettingSlider("distance", 5, 200) {
			@Override
			public String getSliderText() {
				return FormatUtil.translatePre("menu.settingButton", this.getLocalizedName(), FormatUtil.translatePre("strings.distanceShort", String.valueOf((int) this.value)));
			}
		});
	}
	
	public void renderInfo(EntityLivingBase entity, Minecraft mc, float partialTicks) {
		if(enabled && entity instanceof EntityHorse) {
			Tessellator t = Tessellator.getInstance();
			
			glPushMatrix(); {
				ArrayList<String> infoParts = new ArrayList<String>();
				
				EntityHorse horse = (EntityHorse) entity;
				
				if(jump.value) {
					infoParts.add(FormatUtil.translatePre("strings.jump", String.valueOf(Math.round(getJumpHeight(horse) * 1000.0) / 1000.0)));
				}
				if(speed.value) {
					infoParts.add(FormatUtil.translatePre("strings.speed", String.valueOf(Math.round(horse.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() * 43.05 * 100.0) / 100.0)));
				}
				
				int horseWidth = FormatUtil.getLongestWidth(mc.fontRendererObj, infoParts) + 10;
				int horseHeight = infoParts.size() * (mc.fontRendererObj.FONT_HEIGHT + 2) + 8;
				
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
					mc.ingameGUI.drawString(mc.fontRendererObj, infoParts.get(i), 5, 5 + (mc.fontRendererObj.FONT_HEIGHT + 2) * i, RenderUtil.colorRGB(255, 255, 255));
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