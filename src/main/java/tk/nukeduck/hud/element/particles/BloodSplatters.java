package tk.nukeduck.hud.element.particles;

import static tk.nukeduck.hud.BetterHud.MC;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.Ticker;
import tk.nukeduck.hud.util.Ticker.Tickable;

public class BloodSplatters extends HudElement implements Tickable {
	private final SettingChoose density = new SettingChoose("density", "blood.sparse", "blood.normal", "blood.dense", "blood.denser");

	@Override
	public void loadDefaults() {
		setEnabled(true);
		density.index = 1;
	}

	public BloodSplatters() {
		super("bloodSplatters");

		settings.add(density);
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

			if (this.isEnabled()) {
				int max = (int) (2 * ((currentHealth - MC.player.getHealth()) - 1) * (density.index + 1 * 2));
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
	public Bounds render(RenderGameOverlayEvent event, LayoutManager manager) {
		GL11.glEnable(GL11.GL_BLEND);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(HUD_ICONS);
		particleManager.renderAll();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		return null;
	}

	@Override
	public boolean shouldRender() {
		return !particleManager.particles.isEmpty();
	}
}
