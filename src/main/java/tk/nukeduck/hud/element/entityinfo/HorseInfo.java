package tk.nukeduck.hud.element.entityinfo;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static tk.nukeduck.hud.BetterHud.MC;

import java.util.ArrayList;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityHorse;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.events.EntityInfoRenderer;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Util;

public class HorseInfo extends EntityInfo {
	private final SettingBoolean jump = new SettingBoolean("jump");
	private final SettingBoolean speed = new SettingBoolean("speed");

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.set(true);
		jump.set(true);
		speed.set(true);
		distance.set(100.0);
	}

	public HorseInfo() {
		super("horseInfo");

		settings.add(jump);
		settings.add(speed);
	}

	public void render(EntityLivingBase entity, float partialTicks) {
		if(settings.get() && entity instanceof EntityHorse) {
			glPushMatrix(); {
				ArrayList<String> infoParts = new ArrayList<String>();

				if(jump.get()) {
					infoParts.add(jump.getLocalizedName() + ": " + Util.formatToPlaces(getJumpHeight((EntityHorse)entity), 3));
				}
				if(speed.get()) {
					infoParts.add(speed.getLocalizedName() + ": " + Util.formatToPlaces(getSpeed((EntityHorse)entity), 3));
				}

				int horseWidth = getLinesSize(infoParts).x + 10;
				int horseHeight = infoParts.size() * (MC.fontRenderer.FONT_HEIGHT + 2) + 8;
				
				EntityInfoRenderer.billBoard(entity, MC.player, partialTicks);
				
				float scale = 1.0F / horseWidth;
				glScalef(scale, scale, scale);
				
				// Rendering starts
				GlUtil.enableBlendTranslucent();
				GlStateManager.translate(0, -(horseHeight + 5), 0);
				
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
		double yVelocity = horse.getHorseJumpStrength();
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
