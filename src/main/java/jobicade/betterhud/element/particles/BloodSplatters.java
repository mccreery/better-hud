package jobicade.betterhud.element.particles;

import static jobicade.betterhud.BetterHud.MC;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BloodSplatters extends ParticleOverlay {
	public BloodSplatters() {
		super("bloodSplatters");
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityDamage(LivingDamageEvent event) {
		if(event.isCanceled() || !event.getEntity().equals(MC.player)) {
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
