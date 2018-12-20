package jobicade.betterhud.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class RenderMobInfoEvent extends RenderWorldLastEvent {
    private final EntityLivingBase entity;

    RenderMobInfoEvent(RenderWorldLastEvent event, EntityLivingBase entity) {
        super(event.getContext(), event.getPartialTicks());
        this.entity = entity;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }
}
