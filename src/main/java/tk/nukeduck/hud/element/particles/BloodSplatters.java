package tk.nukeduck.hud.element.particles;

import static tk.nukeduck.hud.BetterHud.HUD_ICONS;
import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tk.nukeduck.hud.util.Point;

public class BloodSplatters extends ParticleOverlay {
	public BloodSplatters() {
		super("bloodSplatters");
	}

	@Override
	protected ResourceLocation getTexture() {
		return HUD_ICONS;
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onEntityHurt(LivingHurtEvent event) {
		Point resolution = new Point(new ScaledResolution(MC));

		int spawnMultiplier = (density.getIndex() + 1) * 4;
		int count = spawnMultiplier * (int)event.getAmount();

		for (int i = 0; i < count; i++) {
			particles.add(ParticleBlood.random(resolution.x, resolution.y));
		}
	}
}
