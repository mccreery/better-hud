package tk.nukeduck.hud.element.particles;

import static org.lwjgl.opengl.GL11.glColor4f;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.ExtraGuiElement;
import tk.nukeduck.hud.element.settings.ElementSettingMode;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Textures;

public class ExtraGuiElementWaterDrops extends ExtraGuiElement {
	public ElementSettingMode density;
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		density.index = 1;
	}
	
	@Override
	public String getName() {
		return "waterDrops";
	}
	
	public ExtraGuiElementWaterDrops() {
		this.settings.add(density = new ElementSettingMode("density", new String[] {"blood.sparse", "blood.normal", "blood.dense", "blood.denser"}));
		this.registerUpdates(UpdateSpeed.FASTER);
	}
	
	GuiParticleManager<GuiParticleWater> particleManager = new GuiParticleManager<GuiParticleWater>();
	private boolean isUnderwater = false;
	
	public void update(Minecraft mc) {
		if(mc.theWorld == null || mc.thePlayer == null) return;
		particleManager.update(mc);
		
		EntityPlayer entityplayer = mc.thePlayer;
		
		ScaledResolution scaledresolution = new ScaledResolution(mc);
		int width = scaledresolution.getScaledWidth();
		int height = scaledresolution.getScaledHeight();
		
		if(!isUnderwater) {
			if(entityplayer.isInsideOfMaterial(Material.WATER)) {
				isUnderwater = true;
				particleManager.particles.clear();
			}
		} else if(!entityplayer.isInsideOfMaterial(Material.WATER)) {
			isUnderwater = false;
			
			if(this.enabled) {
				int max = 10 * (density.index + 1);
				for(int i = 0; i < max; i++) {
					particleManager.particles.add(GuiParticleWater.random(width, height));
				}
			}
		}
		
		BlockPos pos = new BlockPos(entityplayer.posX, entityplayer.posY + entityplayer.getEyeHeight(), entityplayer.posZ);
		if(mc.theWorld.isRainingAt(pos) && BetterHud.random.nextInt((4 - this.density.index) * 3) == 0) {
			particleManager.particles.add(GuiParticleWater.random(width, height));
		}
	}
	
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		GL11.glEnable(GL11.GL_BLEND);
		
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(Textures.particles);
		particleManager.renderAll(mc);
		glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public boolean shouldProfile() {
		return particleManager.particles.size() > 0;
	}
}