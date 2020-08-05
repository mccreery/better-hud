package jobicade.betterhud.element.particles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import jobicade.betterhud.util.RandomWrapper;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

public class WaterDrops extends ParticleOverlay {
    public WaterDrops() {
        super("waterDrops");
    }

    private boolean wasUnderwaterLastTick = false;

    @Override
    protected void updateParticles() {
        boolean isUnderwater = Minecraft.getMinecraft().player.isInsideOfMaterial(Material.WATER);

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

            BlockPos camera = new BlockPos(Minecraft.getMinecraft().player.getPositionEyes(1));
            if (Minecraft.getMinecraft().world.isRainingAt(camera)
                    && new RandomWrapper(new Random()).nextTrial(getParticleChance())) {
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
        return new Random().nextInt((density.getIndex() + 1) * 20);
    }
}
