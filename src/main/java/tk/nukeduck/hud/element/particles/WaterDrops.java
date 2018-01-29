package tk.nukeduck.hud.element.particles;

import static org.lwjgl.opengl.GL11.glColor4f;
import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.RANDOM;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Tickable;

public class WaterDrops extends HudElement implements Tickable {
	private final SettingChoose density = new SettingChoose("density", "sparse", "normal", "dense", "denser");

	public WaterDrops() {
		super("waterDrops");

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

	ParticleManager<ParticleWater> particleManager = new ParticleManager<ParticleWater>();
	private boolean isUnderwater = false;

	@Override
	public void tick() {
		if(MC.world == null || MC.player == null) return;
		particleManager.update();

		EntityPlayer entityplayer = MC.player;

		ScaledResolution scaledresolution = new ScaledResolution(MC);
		int width = scaledresolution.getScaledWidth();
		int height = scaledresolution.getScaledHeight();

		if(!isUnderwater) {
			if(entityplayer.isInsideOfMaterial(Material.WATER)) {
				isUnderwater = true;
				particleManager.particles.clear();
			}
		} else if(!entityplayer.isInsideOfMaterial(Material.WATER)) {
			isUnderwater = false;

			if(this.isEnabled()) {
				int max = 10 * (density.getIndex() + 1);
				for(int i = 0; i < max; i++) {
					particleManager.particles.add(ParticleWater.random(width, height));
				}
			}
		}

		BlockPos pos = new BlockPos(entityplayer.posX, entityplayer.posY + entityplayer.getEyeHeight(), entityplayer.posZ);

		// TODO hmm
		if(MC.world.isRainingAt(pos) && RANDOM.nextInt((4 - density.getIndex()) * 3) == 0) {
			particleManager.particles.add(ParticleWater.random(width, height));
		}
	}

	public Bounds render(RenderGameOverlayEvent event) {
		GL11.glEnable(GL11.GL_BLEND);

		FMLClientHandler.instance().getClient().renderEngine.bindTexture(PARTICLES);
		particleManager.renderAll();
		glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		return null;
	}

	@Override
	public boolean shouldRender() {
		return !particleManager.particles.isEmpty();
	}
}
