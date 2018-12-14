package jobicade.betterhud.util.bars;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import jobicade.betterhud.util.geom.Rect;
import jobicade.betterhud.util.geom.Direction;
import jobicade.betterhud.util.MathUtil;

public class StatBarMount extends StatBarBasic<Entity> {
	@Override
	public boolean shouldRender() {
		return host.getRidingEntity() instanceof EntityLivingBase;
	}

	@Override
	protected Rect getIcon(IconType icon, int pointsIndex) {
		switch(icon) {
			case BACKGROUND: return new Rect(52, 9, 9, 9);
			case HALF:       return new Rect(97, 9, 9, 9);
			case FULL:       return new Rect(88, 9, 9, 9);
			default:         return null;
		}
	}

	@Override
	protected int getCurrent() {
		return MathUtil.getHealthForDisplay(((EntityLivingBase)host.getRidingEntity()).getHealth());
	}

	@Override
	protected int getMaximum() {
		return MathUtil.getHealthForDisplay(((EntityLivingBase)host.getRidingEntity()).getMaxHealth());
	}

	@Override
	public Direction getNativeAlignment() {
		return Direction.EAST;
	}
}
