package tk.nukeduck.hud.util.bars;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;

public class StatBarMount extends StatBarSided {
	private final Entity entity;

	public StatBarMount(Entity entity) {
		this.entity = entity;
	}

	@Override
	public boolean shouldRender() {
		return entity.getRidingEntity() instanceof EntityLivingBase;
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
		return (int)((EntityLivingBase)entity.getRidingEntity()).getHealth();
	}

	@Override
	protected int getMaximum() {
		return (int)((EntityLivingBase)entity.getRidingEntity()).getMaxHealth();
	}

	@Override
	public Direction getIconAlignment() {
		return Direction.WEST;
	}
}
