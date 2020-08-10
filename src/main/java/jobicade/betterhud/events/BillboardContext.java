package jobicade.betterhud.events;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public final class BillboardContext {
    private final RenderWorldLastEvent event;
    private final LivingEntity entity;

    public BillboardContext(RenderWorldLastEvent event, LivingEntity entity) {
        this.event = event;
        this.entity = entity;
    }

    public RenderWorldLastEvent getEvent() {
        return event;
    }

    public float getPartialTicks() {
        return event.getPartialTicks();
    }

    public LivingEntity getPointedEntity() {
        return entity;
    }
}
