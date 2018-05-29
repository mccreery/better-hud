package tk.nukeduck.hud.util.bars;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Point;

public class StatBarFood extends StatBarBasic {
	private final EntityPlayer entity;

	private final Random random = new Random();

	public StatBarFood(EntityPlayer entity) {
		this.entity = entity;
	}

	@Override
	protected int getCurrent() {
		return entity.getFoodStats().getFoodLevel();
	}

	@Override
	protected Bounds getIcon(IconType icon, int pointsIndex) {
		switch(icon) {
			case BACKGROUND: return new Bounds(16, 27, 9, 9);
			case HALF:       return new Bounds(61, 27, 9, 9);
			case FULL:       return new Bounds(52, 27, 9, 9);
			default:         return null;
		}
	}

	@Override
	public Direction getNativeAlignment() {
		return Direction.EAST;
	}

	@Override
	protected int getIconBounce(int pointsIndex) {
		if(entity.getFoodStats().getSaturationLevel() <= 0 && MC.ingameGUI.getUpdateCounter() % (getCurrent() * 3 + 1) == 0) {
			return random.nextInt(3) - 1;
		} else {
			return 0;
		}
	}

	@Override
	public void render(Point position, Direction alignment) {
		random.setSeed(MC.ingameGUI.getUpdateCounter());
		super.render(position, alignment);
	}
}
