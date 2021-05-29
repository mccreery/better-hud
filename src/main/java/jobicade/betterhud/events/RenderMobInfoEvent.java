package jobicade.betterhud.events;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class RenderMobInfoEvent extends RenderWorldLastEvent {
    private final LivingEntity entity;

    RenderMobInfoEvent(RenderWorldLastEvent event, LivingEntity entity) {
        super(event.getContext(), event.getMatrixStack(), event.getPartialTicks(), event.getProjectionMatrix(), event.getFinishTimeNano());
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }
}
