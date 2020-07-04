package jobicade.betterhud.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public final class BillboardContext {
    private final RenderWorldLastEvent event;
    private final EntityLivingBase entity;

    public BillboardContext(RenderWorldLastEvent event, EntityLivingBase entity) {
        this.event = event;
        this.entity = entity;
    }

    public RenderWorldLastEvent getEvent() {
        return event;
    }

    public float getPartialTicks() {
        return event.getPartialTicks();
    }

    public EntityLivingBase getPointedEntity() {
        return entity;
    }
}
