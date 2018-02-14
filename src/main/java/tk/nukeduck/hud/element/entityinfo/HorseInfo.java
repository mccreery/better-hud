package tk.nukeduck.hud.element.entityinfo;

import java.util.ArrayList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityHorse;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.PaddedBounds;

public class HorseInfo extends EntityInfo {
	private final SettingBoolean jump = new SettingBoolean("jump");
	private final SettingBoolean speed = new SettingBoolean("speed");

	@Override
	public void loadDefaults() {
		settings.set(true);
		jump.set(true);
		speed.set(true);
	}

	public HorseInfo() {
		super("horseInfo");

		settings.add(jump);
		settings.add(speed);
	}

	public void render(EntityLivingBase entity, float partialTicks) {
		ArrayList<String> infoParts = new ArrayList<String>();

		if(jump.get()) {
			infoParts.add(jump.getLocalizedName() + ": " + FormatUtil.formatToPlaces(getJumpHeight((EntityHorse)entity), 3) + "m");
		}
		if(speed.get()) {
			infoParts.add(speed.getLocalizedName() + ": " + FormatUtil.formatToPlaces(getSpeed((EntityHorse)entity), 3) + "m/s");
		}

		PaddedBounds bounds = new PaddedBounds(new Bounds(GlUtil.getLinesSize(infoParts)), Bounds.PADDING, Bounds.EMPTY);
		BetterHud.MANAGER.position(Direction.SOUTH, bounds);

		GlUtil.drawRect(bounds, Colors.TRANSLUCENT);
		GlUtil.drawLines(infoParts, bounds.contentBounds(), Direction.NORTH_WEST, Colors.WHITE);
	}

	/** Calculates horse jump height using a derived polynomial
	 * @see <a href=https://minecraft.gamepedia.com/Horse#Jump_strength>Minecraft Wiki</a> */
	public double getJumpHeight(EntityHorse horse) {
		double jumpStrength = horse.getHorseJumpStrength();
		return jumpStrength * (jumpStrength * (jumpStrength * -0.1817584952 + 3.689713992) + 2.128599134) - 0.343930367;
	}

	/** Calculates horse speed using an approximate coefficient
	 * @see <a href=https://minecraft.gamepedia.com/Horse#Movement_speed>Minecraft Wiki</a> */
	public double getSpeed(EntityHorse horse) {
		return horse.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() * 43.17037;
	}

	@Override
	public boolean shouldRender(EntityLivingBase entity) {
		return entity instanceof EntityHorse;
	}
}
