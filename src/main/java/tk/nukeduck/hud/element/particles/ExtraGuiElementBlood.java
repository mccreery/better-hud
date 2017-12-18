package tk.nukeduck.hud.element.particles;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.client.FMLClientHandler;
import tk.nukeduck.hud.element.ExtraGuiElement;
import tk.nukeduck.hud.element.settings.ElementSettingMode;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Textures;

public class ExtraGuiElementBlood extends ExtraGuiElement {
	public ElementSettingMode density;
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		density.index = 1;
	}
	
	@Override
	public String getName() {
		return "bloodSplatters";
	}
	
	public ExtraGuiElementBlood() {
		this.settings.add(density = new ElementSettingMode("density", new String[] {"blood.sparse", "blood.normal", "blood.dense", "blood.denser"}));
		this.registerUpdates(UpdateSpeed.FASTER);
	}
	
	float currentHealth;
	GuiParticleManager<GuiParticleBlood> particleManager = new GuiParticleManager<GuiParticleBlood>();
	
	public void update(Minecraft mc) {
		particleManager.update(mc);
		if(mc.player == null) return;
		
		if (currentHealth == -1f) {
			currentHealth = mc.player.getHealth();
		} else if(mc.player.getHealth() < currentHealth) {
			ScaledResolution scaledresolution = new ScaledResolution(mc);
			int width = scaledresolution.getScaledWidth();
			int height = scaledresolution.getScaledHeight();
			
			if (this.enabled) {
				int max = (int) (2 * ((currentHealth - mc.player.getHealth()) - 1) * (density.index + 1 * 2));
				for (int i = 0; i < max; i++) {
					particleManager.particles.add(GuiParticleBlood.random(width, height));
				}
			}

			currentHealth = mc.player.getHealth();
		} else if (mc.player.getHealth() > currentHealth) {
			currentHealth = mc.player.getHealth();
		}
	}
	
	@Override
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		GL11.glEnable(GL11.GL_BLEND);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(Textures.iconsHud);
		particleManager.renderAll(mc);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public boolean shouldProfile() {
		return particleManager.particles.size() > 0;
	}
}