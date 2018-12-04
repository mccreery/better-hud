package tk.nukeduck.hud.element.particles;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.Setting;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Tickable;

public abstract class ParticleOverlay extends HudElement implements Tickable {
	protected SettingChoose density;
	protected final List<Particle> particles = new CopyOnWriteArrayList<Particle>();

	protected ParticleOverlay(String name) {
		super(name);
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(density = new SettingChoose("density", "sparse", "normal", "dense", "denser"));
	}

	/** Called each tick while enabled to spawn new particles.
	 * Default implementation kills dead particles */
	protected void updateParticles() {
		List<Particle> dead = new ArrayList<>(particles.size());

		for(Particle particle : particles) {
			if(particle.isDead()) dead.add(particle);
		}
		particles.removeAll(dead);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		Ticker.FASTER.register(this);
	}

	@Override
	public void tick() {
		if(!isEnabledAndSupported() || MC.player == null || MC.world == null) return;

		particles.forEach(Particle::tick);
		updateParticles();
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		density.setIndex(1);
	}

	@Override
	public Bounds render(Event event) {
		for(Particle particle : particles) {
			particle.render(getPartialTicks(event));
		}
		return MANAGER.getScreen();
	}

	@Override
	public boolean shouldRender(Event event) {
		return !particles.isEmpty();
	}
}
