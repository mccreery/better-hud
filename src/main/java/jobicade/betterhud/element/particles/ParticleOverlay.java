package jobicade.betterhud.element.particles;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.registry.OverlayElements;
import jobicade.betterhud.util.Tickable;

public abstract class ParticleOverlay extends OverlayElement implements Tickable {
    protected SettingChoose density;
    protected final List<Particle> particles = new CopyOnWriteArrayList<Particle>();

    public ParticleOverlay(String name) {
        super(name);
        density = new SettingChoose("density", "sparse", "normal", "dense", "denser");
        addSetting(density);
    }

    /** Called each tick while enabled to spawn new particles.
     * Default implementation kills dead particles */
    protected void updateParticles() {
        particles.removeIf(Particle::isDead);
    }

    @Override
    public void tick() {
        if (OverlayElements.get().getEnabled().contains(this)
                && !MC.isGamePaused()) {
            particles.forEach(Particle::tick);
            updateParticles();
        }
    }

    @Override
    public Rect render(OverlayContext context) {
        for(Particle particle : particles) {
            particle.render(context.getPartialTicks());
        }
        return MANAGER.getScreen();
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return !particles.isEmpty();
    }
}
