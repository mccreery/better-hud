package jobicade.betterhud.element.text;

import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.geom.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.eventbus.api.Event;

import java.util.Arrays;
import java.util.List;

public class FullInvIndicator extends TextElement {
    private SettingBoolean offHand;

    @Override
    public void loadDefaults() {
        super.loadDefaults();

        position.setPreset(Direction.NORTH_EAST);
        offHand.set(false);
    }

    public FullInvIndicator() {
        super("fullInvIndicator");
    }

    @Override
    protected void addSettings(List<Setting<?>> settings) {
        super.addSettings(settings);
        settings.add(new Legend("misc"));
        settings.add(offHand = new SettingBoolean("offhand"));
    }

    @Override
    protected List<String> getText() {
        return Arrays.asList(I18n.get("betterHud.hud.fullInv"));
    }

    @Override
    public boolean shouldRender(Event event) {
        return super.shouldRender(event) && Minecraft.getInstance().player.inventory.getFreeSlot() == -1 &&
            (!offHand.get() || !Minecraft.getInstance().player.inventory.offhand.get(0).isEmpty());
    }
}
