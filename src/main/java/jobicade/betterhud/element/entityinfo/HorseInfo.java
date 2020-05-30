package jobicade.betterhud.element.entityinfo;

import java.util.ArrayList;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.events.RenderMobInfoEvent;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.Grid;
import jobicade.betterhud.render.Label;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.MathUtil;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityHorse;

public class HorseInfo extends EntityInfo {
	private SettingBoolean jump, speed;

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		jump.set(true);
		speed.set(true);
	}

	public HorseInfo() {
		setRegistryName("horse_info");
		setUnlocalizedName("horseInfo");

		settings.addChildren(
			jump = new SettingBoolean("jump"),
			speed = new SettingBoolean("speed")
		);
	}

	@Override
	public boolean shouldRender(RenderMobInfoEvent context) {
		return context.getEntity() instanceof EntityHorse;
	}

	@Override
	public Rect render(RenderMobInfoEvent context) {
		ArrayList<Label> infoParts = new ArrayList<Label>();
		EntityHorse entity = (EntityHorse)context.getEntity();

		if(jump.get()) {
			infoParts.add(new Label(jump.getLocalizedName() + ": " + MathUtil.formatToPlaces(getJumpHeight(entity), 3) + "m"));
		}
		if(speed.get()) {
			infoParts.add(new Label(speed.getLocalizedName() + ": " + MathUtil.formatToPlaces(getSpeed(entity), 3) + "m/s"));
		}

		Grid<Label> grid = new Grid<Label>(new Point(1, infoParts.size()), infoParts).setGutter(new Point(2, 2));
		Rect bounds = new Rect(grid.getPreferredSize().add(10, 10));
		bounds = BetterHud.MANAGER.position(Direction.SOUTH, bounds);

		GlUtil.drawRect(bounds, Color.TRANSLUCENT);
		grid.setBounds(new Rect(grid.getPreferredSize()).anchor(bounds, Direction.CENTER)).render();
		return null;
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
}
