package jobicade.betterhud.element.particles;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.ArrayList;
import java.util.Collection;

public class BloodSplatters extends ParticleOverlay {
    public BloodSplatters() {
        super("bloodSplatters");
    }

    @Override
    public void init(FMLClientSetupEvent event) {
        super.init(event);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityDamage(LivingDamageEvent event) {
        if(event.isCanceled() || !event.getEntity().equals(Minecraft.getInstance().player)) {
            return;
        }

        int spawnMultiplier = (density.getIndex() + 1) * 4;
        int count = spawnMultiplier * (int)event.getAmount();

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
