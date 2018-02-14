package tk.nukeduck.hud.element.particles;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.RANDOM;

import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.BlockPos;
import tk.nukeduck.hud.util.Point;

public class WaterDrops extends ParticleOverlay {
	public WaterDrops() {
		super("waterDrops");
	}

	private boolean wasUnderwaterLastTick = false;

	@Override
	protected void spawnTick() {
		Point resolution = new Point(new ScaledResolution(MC));
		boolean isUnderwater = MC.player.isInsideOfMaterial(Material.WATER);

		if(wasUnderwaterLastTick != isUnderwater) {
			if(wasUnderwaterLastTick) {
				int count = getParticleCount();

				for(int i = 0; i < count; i++) {
					particles.add(ParticleWater.random(resolution.x, resolution.y));
				}
			} else {
				particles.clear();
			}
		}
		wasUnderwaterLastTick = isUnderwater;

		BlockPos camera = new BlockPos(MC.player.getPositionEyes(1));

		if(MC.world.isRainingAt(camera) && RANDOM.nextFloat() < getParticleChance()) {
			particles.add(ParticleWater.random(resolution.x, resolution.y));
		}
	}

	private float getParticleChance() {
		return 0.2f + density.getIndex() * 0.15f;
	}

	private int getParticleCount() {
		return RANDOM.nextInt(20 * (density.getIndex() + 1));
	}
}
