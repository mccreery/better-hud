package jobicade.betterhud.element.particles;

import java.util.ArrayList;
import java.util.Collection;

public class BloodSplatters extends ParticleOverlay {
    public BloodSplatters() {
        super("bloodSplatters");
    }

    public void onDamaged(int amount) {
        int spawnMultiplier = (density.getIndex() + 1) * 4;
        int count = spawnMultiplier * amount;

        if(count > 0) {
            Collection<Particle> toSpawn = new ArrayList<>(count);
            for(int i = 0; i < count; i++) {
                toSpawn.add(ParticleBase.createRandom());
            }

            // Atomic operation means underlying CopyOnWriteArrayList only copies once
            particles.addAll(toSpawn);
        }
    }
}
