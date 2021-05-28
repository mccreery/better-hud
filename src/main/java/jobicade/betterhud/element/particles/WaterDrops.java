package jobicade.betterhud.element.particles;

import jobicade.betterhud.util.MathUtil;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collection;

public class WaterDrops extends ParticleOverlay {
    public WaterDrops() {
        super("waterDrops");
    }

    private boolean wasUnderwaterLastTick = false;

    @Override
    protected void updateParticles() {
        boolean isUnderwater = Minecraft.getInstance().player.func_70055_a(Material.WATER);

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

            BlockPos camera = new BlockPos(Minecraft.getInstance().player.getEyePosition(1));
            if(Minecraft.getInstance().level.isRainingAt(camera) && MathUtil.randomChance(getParticleChance())) {
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
