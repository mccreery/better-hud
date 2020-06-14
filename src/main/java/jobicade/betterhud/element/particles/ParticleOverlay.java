package jobicade.betterhud.element.particles;

import static jobicade.betterhud.BetterHud.MANAGER;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.Tickable;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public abstract class ParticleOverlay extends OverlayElement implements Tickable {
	protected SettingChoose density;
	protected final List<Particle> particles = new CopyOnWriteArrayList<Particle>();

	public ParticleOverlay(String name) {
		super(name);
		settings.addChild(density = new SettingChoose("density", "sparse", "normal", "dense", "denser"));
	}

	/** Called each tick while enabled to spawn new particles.
	 * Default implementation kills dead particles */
	protected void updateParticles() {
		particles.removeIf(Particle::isDead);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		Ticker.FASTER.register(this);
	}

	@Override
	public void tick() {
		if (OverlayHook.shouldRender(this, null)) {
			particles.forEach(Particle::tick);
			updateParticles();
		}
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		density.setIndex(1);
	}

	@Override
	public Rect render(RenderGameOverlayEvent context) {
		for(Particle particle : particles) {
			particle.render(context.getPartialTicks());
		}
		return MANAGER.getScreen();
	}

	@Override
	public boolean shouldRender(RenderGameOverlayEvent context) {
		return !particles.isEmpty();
	}
}
