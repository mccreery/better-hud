package jobicade.betterhud.element.entityinfo;

import static jobicade.betterhud.BetterHud.SPACER;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityHorse;
import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.util.geom.Rect;
import jobicade.betterhud.util.render.Color;
import jobicade.betterhud.util.geom.Direction;
import jobicade.betterhud.util.FormatUtil;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.StringGroup;

public class HorseInfo extends EntityInfo {
	private SettingBoolean jump, speed;

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		jump.set(true);
		speed.set(true);
	}

	public HorseInfo() {
		super("horseInfo");
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);

		settings.add(jump = new SettingBoolean("jump"));
		settings.add(speed = new SettingBoolean("speed"));
	}

	@Override
	public void render(EntityLivingBase entity) {
		ArrayList<String> infoParts = new ArrayList<String>();

		if(jump.get()) {
			infoParts.add(jump.getLocalizedName() + ": " + FormatUtil.formatToPlaces(getJumpHeight((EntityHorse)entity), 3) + "m");
		}
		if(speed.get()) {
			infoParts.add(speed.getLocalizedName() + ": " + FormatUtil.formatToPlaces(getSpeed((EntityHorse)entity), 3) + "m/s");
		}

		StringGroup group = new StringGroup(infoParts);

		Rect bounds = new Rect(group.getSize().add(Rect.createPadding(SPACER).getSize()));
		bounds = BetterHud.MANAGER.position(Direction.SOUTH, bounds);

		GlUtil.drawRect(bounds, Color.TRANSLUCENT);
		group.draw(bounds.grow(-SPACER));
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
