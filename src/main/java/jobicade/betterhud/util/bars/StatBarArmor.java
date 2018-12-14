package jobicade.betterhud.util.bars;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.ForgeHooks;
import jobicade.betterhud.util.geom.Rect;
import jobicade.betterhud.util.geom.Direction;

public class StatBarArmor extends StatBarBasic<EntityPlayer> {
	@Override
	protected int getCurrent() {
		return ForgeHooks.getTotalArmorValue(host);
	}

	@Override
	protected Rect getIcon(IconType icon, int pointsIndex) {
		switch(icon) {
			case BACKGROUND: return new Rect(16, 9, 9, 9);
			case HALF:       return new Rect(25, 9, 9, 9);
			case FULL:       return new Rect(34, 9, 9, 9);
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
