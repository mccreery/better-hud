package tk.nukeduck.hud.element.particles;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.PARTICLES;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Tickable;

public abstract class ParticleOverlay extends HudElement implements Tickable {
	protected final SettingChoose density = new SettingChoose("density", "sparse", "normal", "dense", "denser");
	protected final ArrayList<Particle> particles = new ArrayList<Particle>();

	protected ParticleOverlay(String name) {
		super(name);
		settings.add(density);
	}

	protected void spawnTick() {}

	protected ResourceLocation getTexture() {
		return PARTICLES;
	}

	@Override
	public void init(FMLInitializationEvent event) {
		Ticker.FASTER.register(this);
	}

	@Override
	public void tick() {
		if(isEnabled() && MC.world != null && MC.player != null) {
			for(Iterator<Particle> it = particles.iterator(); it.hasNext(); ) {
				if(it.next().update()) it.remove();
			}
			spawnTick();
		}
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		density.setIndex(1);
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event) {
		GlUtil.enableBlendTranslucent();
		MC.getTextureManager().bindTexture(getTexture());

		for(Particle particle : particles) {
			particle.render();
		}

		GlUtil.color(Colors.WHITE);
		return null;
	}

	@Override
	public boolean shouldRender() {
		return !particles.isEmpty();
	}
}
