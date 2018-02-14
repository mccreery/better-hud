package tk.nukeduck.hud.element.particles;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.PARTICLES;
import static tk.nukeduck.hud.BetterHud.RANDOM;

import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.GlUtil;
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
				int count = getParticleCount();

				for(int i = 0; i < count; i++) {
					particleManager.particles.add(ParticleWater.random(width, height));
				}
			}
		}
		BlockPos pos = new BlockPos(entityplayer.posX, entityplayer.posY + entityplayer.getEyeHeight(), entityplayer.posZ);

		if(MC.world.isRainingAt(pos) && RANDOM.nextFloat() < getParticleChance()) {
			particleManager.particles.add(ParticleWater.random(width, height));
		}
	}

	private float getParticleChance() {
		return 0.2f + density.getIndex() * 0.15f;
	}

	private int getParticleCount() {
		return RANDOM.nextInt(20 * (density.getIndex() + 1));
	}

	public Bounds render(RenderGameOverlayEvent event) {
		GlUtil.enableBlendTranslucent();
		MC.getTextureManager().bindTexture(PARTICLES);

		particleManager.renderAll();

		GlUtil.color(Colors.WHITE);
		return null;
	}

	@Override
	public boolean shouldRender() {
		return !particleManager.particles.isEmpty();
	}
}
