package jobicade.betterhud.util.bars;

import static jobicade.betterhud.BetterHud.MC;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.util.MathUtil;

public class StatBarFood extends StatBarBasic<EntityPlayer> {
	private final Random random = new Random();

	@Override
	protected int getCurrent() {
		return host.getFoodStats().getFoodLevel();
	}

	@Override
	protected Rect getIcon(IconType icon, int pointsIndex) {
		boolean hasHunger = host.isPotionActive(MobEffects.HUNGER);
		int xOffset = hasHunger ? 88 : 52;

		switch(icon) {
			case BACKGROUND: return new Rect(hasHunger ? 133 : 16, 27, 9, 9);
			case HALF:       return new Rect(xOffset + 9, 27, 9, 9);
			case FULL:       return new Rect(xOffset, 27, 9, 9);
			default:         return null;
		}
	}

	@Override
	public Direction getNativeAlignment() {
		return Direction.EAST;
	}

	@Override
	protected int getIconBounce(int pointsIndex) {
		if(host.getFoodStats().getSaturationLevel() <= 0 && MC.ingameGUI.getTicks() % (getCurrent() * 3 + 1) == 0) {
			return MathUtil.randomRange(-1, 2);
		} else {
			return 0;
		}
	}

	@Override
	public void render() {
		random.setSeed(MC.ingameGUI.getTicks());
		MathUtil.setRandom(random);

		super.render();

		MathUtil.setRandom(null);
	}
}
