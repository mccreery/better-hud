package tk.nukeduck.hud.util.bars;

import net.minecraft.entity.EntityLivingBase;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;

public class StatBarArmor extends StatBarBasic {
	private final EntityLivingBase entity;

	public StatBarArmor(EntityLivingBase entity) {
		this.entity = entity;
	}

	@Override
	protected int getCurrent() {
		return entity.getTotalArmorValue();
	}

	@Override
	protected Bounds getIcon(IconType icon, int pointsIndex) {
		switch(icon) {
			case BACKGROUND: return new Bounds(16, 9, 9, 9);
			case HALF:       return new Bounds(25, 9, 9, 9);
			case FULL:       return new Bounds(34, 9, 9, 9);
			default:         return null;
		}
	}

	@Override
	public boolean shouldRender() {
		return getCurrent() > 0;
	}

	@Override
	public Direction getNativeAlignment() {
		return Direction.WEST;
	}
}
