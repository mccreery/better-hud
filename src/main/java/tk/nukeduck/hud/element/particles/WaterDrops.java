package tk.nukeduck.hud.element.particles;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import tk.nukeduck.hud.util.MathUtil;

public class WaterDrops extends ParticleOverlay {
	public WaterDrops() {
		super("waterDrops");
	}

	private boolean wasUnderwaterLastTick = false;

	@Override
	protected void updateParticles() {
		boolean isUnderwater = MC.player.isInsideOfMaterial(Material.WATER);

		if(isUnderwater) {
			particles.clear();
		} else {
			super.updateParticles();
			Collection<Particle> toSpawn = new ArrayList<>();

			if(wasUnderwaterLastTick) {
				int count = getParticleCount();

				for(int i = 0; i < count; i++) {
					toSpawn.add(ParticleWater.createRandom());
				}
			}

			BlockPos camera = new BlockPos(MC.player.getPositionEyes(1));
			if(MC.world.isRainingAt(camera) && MathUtil.randomChance(getParticleChance())) {
				toSpawn.add(ParticleWater.createRandom());
			}

			// Atomic operation means underlying CopyOnWriteArrayList only copies once
			particles.addAll(toSpawn);
		}
		wasUnderwaterLastTick = isUnderwater;
	}

	private float getParticleChance() {
		return 0.2f + density.getIndex() * 0.15f;
	}

	private int getParticleCount() {
		return MathUtil.randomRange(0, (density.getIndex() + 1) * 20);
	}
}
