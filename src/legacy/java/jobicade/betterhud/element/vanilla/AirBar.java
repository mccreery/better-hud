package jobicade.betterhud.element.vanilla;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.util.bars.StatBarAir;

public class AirBar extends Bar {
    public AirBar() {
        super("airBar", new StatBarAir());
    }

    @Override
    public void loadDefaults() {
        super.loadDefaults();
        settings.priority.set(4);
        side.setIndex(1);
    }

    @Override
    protected ElementType getType() {
        return ElementType.AIR;
    }

    @Override
    public boolean shouldRender(Event event) {
        return Minecraft.getMinecraft().playerController.shouldDrawHUD() && super.shouldRender(event);
    }
}
