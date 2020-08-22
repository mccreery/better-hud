package jobicade.betterhud.element.text;

import static jobicade.betterhud.BetterHud.MC;

import java.util.Arrays;
import java.util.List;

import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.events.OverlayContext;
import net.minecraft.client.resources.I18n;

public class FullInvIndicator extends TextElement {
    private SettingBoolean offHand;

    public FullInvIndicator() {
        super("fullInvIndicator");

        addSetting(new Legend("misc"));
        offHand = new SettingBoolean("offhand");
        addSetting(offHand);
    }

    @Override
    protected List<String> getText() {
        return Arrays.asList(I18n.format("betterHud.hud.fullInv"));
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return MC.player.inventory.getFirstEmptyStack() == -1 &&
            (!offHand.get() || !MC.player.inventory.offHandInventory.get(0).isEmpty());
    }
}
