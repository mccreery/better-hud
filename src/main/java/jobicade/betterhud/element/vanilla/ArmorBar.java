package jobicade.betterhud.element.vanilla;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.util.bars.StatBarArmor;

public class ArmorBar extends Bar {
    public ArmorBar() {
        super("armor", new StatBarArmor());
    }

    @Override
    public void loadDefaults() {
        super.loadDefaults();
        settings.priority.set(4);
        side.setIndex(0);
    }

    @Override
    protected ElementType getType() {
        return ElementType.ARMOR;
    }

    @Override
    public boolean shouldRender(Event event) {
        return Minecraft.getInstance().gameMode.canHurtPlayer() && super.shouldRender(event);
    }
}
