package tk.nukeduck.hud.util.bars;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.ForgeHooks;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;

public class StatBarArmor extends StatBarBasic<EntityPlayer> {
	@Override
	protected int getCurrent() {
		return ForgeHooks.getTotalArmorValue(host);
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
