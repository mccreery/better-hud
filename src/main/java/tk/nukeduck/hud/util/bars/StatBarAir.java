package tk.nukeduck.hud.util.bars;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import tk.nukeduck.hud.util.Bounds;

public class StatBarAir extends StatBarBasic {
	private final Entity entity;

	public StatBarAir(Entity entity) {
		this.entity = entity;
	}

	@Override
	protected int getCurrent() {
		int air = entity.getAir();

		int full = ((air - 2) * 10 + 299) / 300;
		int partial = (air * 10 + 299) / 300 - full;
		return full * 2 + partial;
	}

	@Override
	protected Bounds getIcon(IconType icon, int pointsIndex) {
		switch(icon) {
			case HALF: return new Bounds(25, 18, 9, 9);
			case FULL: return new Bounds(16, 18, 9, 9);
			case BACKGROUND: default: return null;
		}
	}

	@Override
	public boolean shouldRender() {
		return entity.isInsideOfMaterial(Material.WATER);
	}
}
