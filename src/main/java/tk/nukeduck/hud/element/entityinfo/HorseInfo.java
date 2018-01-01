package tk.nukeduck.hud.element.entityinfo;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static tk.nukeduck.hud.BetterHud.MC;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityHorse;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.RenderUtil;

public class HorseInfo extends EntityInfo {
	private final SettingBoolean jump = new SettingBoolean("jump");
	private final SettingBoolean speed = new SettingBoolean("speed");

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.set(true);
		jump.set(true);
		speed.set(true);
		distance.value = 100;
	}

	public HorseInfo() {
		super("horseInfo");

		settings.add(jump);
		settings.add(speed);
	}

	public void renderInfo(EntityLivingBase entity, float partialTicks) {
		if(settings.get() && entity instanceof EntityHorse) {
			glPushMatrix(); {
				ArrayList<String> infoParts = new ArrayList<String>();
				
				EntityHorse horse = (EntityHorse) entity;

				if(jump.get())  infoParts.add(I18n.format("betterHud.strings.jump", Math.round(getJumpHeight(horse) * 1000.0d) / 1000.0d));
				if(speed.get()) infoParts.add(I18n.format("betterHud.strings.speed", Math.round(getSpeed(horse) * 1000.0d) / 1000.0d));

				int horseWidth = FormatUtil.getLongestWidth(MC.fontRenderer, infoParts) + 10;
				int horseHeight = infoParts.size() * (MC.fontRenderer.FONT_HEIGHT + 2) + 8;
				
				RenderUtil.billBoard(entity, MC.player, partialTicks);
				
				float scale = 1.0F / horseWidth;
				glScalef(scale, scale, scale);
				
				// Rendering starts
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

				glTranslatef(0.0F, -horseHeight - 5, 0.0F);

				Gui.drawRect(0, 0, horseWidth, horseHeight, Colors.TRANSLUCENT);
				zIncrease();
				for(int i = 0; i < infoParts.size(); i++) {
					MC.ingameGUI.drawString(MC.fontRenderer, infoParts.get(i), 5, 5 + (MC.fontRenderer.FONT_HEIGHT + 2) * i, Colors.WHITE);
				}
			}
			glPopMatrix();
		}
	}
	
	// TODO might want to check these matey
	public double getJumpHeight(EntityHorse horse) {
		double yVelocity = horse.getHorseJumpStrength(); //horses's jump strength attribute
		double jumpHeight = 0;
		while(yVelocity > 0) {
			jumpHeight += yVelocity;
			yVelocity -= 0.08;
			yVelocity *= 0.98;
		}
		return jumpHeight;
	}

	public double getSpeed(EntityHorse horse) {
		return horse.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() * 43.05;
	}
}
