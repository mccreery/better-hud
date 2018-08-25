package tk.nukeduck.hud.util.bars;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.MathUtil;

public class StatBarMount extends StatBarBasic<Entity> {
	@Override
	public boolean shouldRender() {
		return host.getRidingEntity() instanceof EntityLivingBase;
	}

	@Override
	protected Bounds getIcon(IconType icon, int pointsIndex) {
		switch(icon) {
			case BACKGROUND: return new Bounds(52, 9, 9, 9);
			case HALF:       return new Bounds(97, 9, 9, 9);
			case FULL:       return new Bounds(88, 9, 9, 9);
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
