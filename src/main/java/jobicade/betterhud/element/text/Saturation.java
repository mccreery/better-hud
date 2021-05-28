package jobicade.betterhud.element.text;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.util.MathUtil;

public class Saturation extends TextElement {
    public Saturation() {
        super("saturation");
    }

    @Override
    public void loadDefaults() {
        super.loadDefaults();
        position.setPreset(Direction.SOUTH_EAST);
    }

    @Override
    public boolean shouldRender(Event event) {
        return super.shouldRender(event) && Minecraft.getInstance().gameMode.hasExperience();
    }

    @Override
    protected List<String> getText() {
        return Arrays.asList(I18n.get("betterHud.hud.saturation", MathUtil.formatToPlaces(Minecraft.getInstance().player.getFoodData().getSaturationLevel(), 1)));
    }
}
