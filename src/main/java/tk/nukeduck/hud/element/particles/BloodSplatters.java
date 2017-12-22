package tk.nukeduck.hud.element.particles;

import static tk.nukeduck.hud.BetterHud.MC;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.SettingMode;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.Ticker;
import tk.nukeduck.hud.util.constants.Textures;

public class BloodSplatters extends HudElement {
	public SettingMode density;
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		density.index = 1;
	}
	
	public BloodSplatters() {
		super("bloodSplatters");
		this.settings.add(density = new SettingMode("density", new String[] {"blood.sparse", "blood.normal", "blood.dense", "blood.denser"}));
		Ticker.FASTER.register(this);
	}
	
	float currentHealth;
	ParticleManager<ParticleBlood> particleManager = new ParticleManager<ParticleBlood>();
	
	public void update() {
		particleManager.update();
		if(MC.player == null) return;
		
		if (currentHealth == -1f) {
			currentHealth = MC.player.getHealth();
		} else if(MC.player.getHealth() < currentHealth) {
			ScaledResolution scaledresolution = new ScaledResolution(MC);
			int width = scaledresolution.getScaledWidth();
			int height = scaledresolution.getScaledHeight();
			
			if (this.enabled) {
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
	
	@Override
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {
		GL11.glEnable(GL11.GL_BLEND);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(Textures.iconsHud);
		particleManager.renderAll();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public boolean shouldProfile() {
		return !particleManager.particles.isEmpty();
	}
}
