package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.util.bars.StatBarFood;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

public class FoodBar extends Bar {
    private SettingBoolean hideMount;

    public FoodBar() {
        super("food", new StatBarFood());
    }

    @Override
    protected void addSettings(List<Setting<?>> settings) {
        super.addSettings(settings);
        settings.add(hideMount = new SettingBoolean("hideMount"));
    }

    @Override
    public void loadDefaults() {
        super.loadDefaults();

        settings.priority.set(3);
        side.setIndex(1);
        hideMount.set(true);
    }

    @Override
    protected ElementType getType() {
        return ElementType.FOOD;
    }

    @Override
    public boolean shouldRender(Event event) {
        return Minecraft.getInstance().gameMode.canHurtPlayer()
            && (!hideMount.get() || !Minecraft.getInstance().player.isRidingJumpable())
            && super.shouldRender(event);
    }
}
