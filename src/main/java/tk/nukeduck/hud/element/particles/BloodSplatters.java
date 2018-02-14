package tk.nukeduck.hud.element.particles;

import static tk.nukeduck.hud.BetterHud.HUD_ICONS;
import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Tickable;

public class BloodSplatters extends HudElement implements Tickable {
	private final SettingChoose density = new SettingChoose("density", "sparse", "normal", "dense", "denser");

	public BloodSplatters() {
		super("bloodSplatters");
		settings.add(density);
	}

	@Override
	public void loadDefaults() {
		settings.set(true);
		density.setIndex(1);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		Ticker.FASTER.register(this);
	}

	float currentHealth;
	ParticleManager<ParticleBlood> particleManager = new ParticleManager<ParticleBlood>();

	@Override
	public void tick() {
		particleManager.update();
		if(MC.player == null) return;

		if (currentHealth == -1f) {
			currentHealth = MC.player.getHealth();
		} else if(MC.player.getHealth() < currentHealth) {
			ScaledResolution scaledresolution = new ScaledResolution(MC);
			int width = scaledresolution.getScaledWidth();
			int height = scaledresolution.getScaledHeight();

			if (isEnabled()) {
				int spawnMultiplier = (density.getIndex() + 1) * 4;
				int healthDelta = (int)(currentHealth - MC.player.getHealth());

				int max = spawnMultiplier * healthDelta;

				for (int i = 0; i < max; i++) {
					particleManager.particles.add(ParticleBlood.random(width, height));
				}
			}

			currentHealth = MC.player.getHealth();
		} else if (MC.player.getHealth() > currentHealth) {
			currentHealth = MC.player.getHealth();
		}
	}

	// TODO make a particles superclass
	@Override
	public Bounds render(RenderGameOverlayEvent event) {
		GlUtil.enableBlendTranslucent();
		MC.getTextureManager().bindTexture(HUD_ICONS);
		particleManager.renderAll();
		GlUtil.color(Colors.WHITE);
		return null;
	}

	@Override
	public boolean shouldRender() {
		return !particleManager.particles.isEmpty();
	}
}
