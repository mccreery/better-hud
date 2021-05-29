package jobicade.betterhud.element.text;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.eventbus.api.Event;

import java.util.Arrays;
import java.util.List;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.SPACER;

public class Distance extends TextElement {
    private SettingChoose mode;

    @Override
    public void loadDefaults() {
        super.loadDefaults();

        position.setPreset(Direction.CENTER);
        mode.setIndex(0);
    }

    public Distance() {
        super("distance", new SettingPosition(DirectionOptions.X, DirectionOptions.WEST_EAST));
    }

    @Override
    protected void addSettings(List<Setting<?>> settings) {
        super.addSettings(settings);
        settings.add(new Legend("misc"));
        settings.add(mode = new SettingChoose(3));
    }

    @Override
    protected Rect moveRect(Rect bounds) {
        if(position.isDirection(Direction.CENTER)) {
            return bounds.align(MANAGER.getScreen().getAnchor(Direction.CENTER).sub(SPACER, SPACER), Direction.SOUTH_EAST);
        } else {
            return super.moveRect(bounds);
        }
    }

    @Override
    protected Rect getPadding() {
        return Rect.createPadding(border ? 2 : 0);
    }

    @Override
    protected Rect render(Event event, List<String> text) {
        border = mode.getIndex() == 2;
        return super.render(event, text);
    }

    @Override
    protected List<String> getText() {
        RayTraceResult trace = Minecraft.getInstance().getCameraEntity().func_174822_a(200, 1.0F);

        if(trace != null) {
            long distance = Math.round(Math.sqrt(trace.func_178782_a().func_177957_d(Minecraft.getInstance().player.getX(), Minecraft.getInstance().player.getY(), Minecraft.getInstance().player.getZ())));

            if(mode.getIndex() == 2) {
                return Arrays.asList(String.valueOf(distance));
            } else {
                return Arrays.asList(I18n.get("betterHud.hud.distance." + mode.getIndex(), String.valueOf(distance)));
            }
        } else {
            return null;
        }
    }
}
