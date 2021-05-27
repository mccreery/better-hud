package jobicade.betterhud.element.particles;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import jobicade.betterhud.util.MathUtil;

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
            if(Minecraft.getMinecraft().world.isRainingAt(camera) && MathUtil.randomChance(getParticleChance())) {
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
