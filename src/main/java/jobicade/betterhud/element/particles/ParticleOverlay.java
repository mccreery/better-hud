package jobicade.betterhud.element.particles;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.Tickable;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static jobicade.betterhud.BetterHud.MANAGER;

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
        particles.removeIf(Particle::isDead);
    }

    @Override
    public void init(FMLClientSetupEvent event) {
        Ticker.FASTER.register(this);
    }

    @Override
    public void tick() {
        if(!isEnabledAndSupported() || Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) return;

        particles.forEach(Particle::tick);
        updateParticles();
    }

    @Override
    public void loadDefaults() {
        super.loadDefaults();
        density.setIndex(1);
    }

    @Override
    public Rect render(Event event) {
        for(Particle particle : particles) {
            particle.render(((RenderGameOverlayEvent)event).getMatrixStack(), getPartialTicks(event));
        }
        return MANAGER.getScreen();
    }

    @Override
    public boolean shouldRender(Event event) {
        return super.shouldRender(event) && !particles.isEmpty();
    }
}
