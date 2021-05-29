package jobicade.betterhud.element.text;

import com.mojang.blaze3d.matrix.MatrixStack;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.element.settings.SettingSlider;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;
import jobicade.betterhud.render.Grid;
import jobicade.betterhud.render.Label;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.Event;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static jobicade.betterhud.BetterHud.MANAGER;

public class Coordinates extends TextElement {
    private SettingBoolean spaced;
    private SettingSlider decimalPlaces;

    @Override
    public void loadDefaults() {
        super.loadDefaults();

        position.setPreset(Direction.NORTH);
        spaced.set(true);
        decimalPlaces.set(0);
        settings.priority.set(-2);
    }

    public Coordinates() {
        super("coordinates", new SettingPosition(DirectionOptions.TOP_BOTTOM, DirectionOptions.NONE));
    }

    @Override
    protected void addSettings(List<Setting<?>> settings) {
        super.addSettings(settings);
        settings.add(new Legend("misc"));
        settings.add(spaced = new SettingBoolean("spaced"));
        settings.add(decimalPlaces = new SettingSlider("precision", 0, 5, 1).setUnlocalizedValue("betterHud.value.places"));
    }

    @Override
    public Rect render(Event event, List<String> text) {
        if(!spaced.get() || !position.isDirection(Direction.NORTH) && !position.isDirection(Direction.SOUTH)) {
            return super.render(event, text);
        }

        MatrixStack matrixStack = ((RenderGameOverlayEvent)event).getMatrixStack();
        Grid<Label> grid = new Grid<>(new Point(3, 1), text.stream().map(s -> new Label(matrixStack, s)).collect(Collectors.toList()))
            .setCellAlignment(position.getDirection()).setGutter(new Point(5, 5));

        Size size = grid.getPreferredSize();
        if(size.getX() < 150) size = size.withX(150);
        Rect bounds = MANAGER.position(position.getDirection(), new Rect(size));

        grid.setBounds(bounds).render();
        return bounds;
    }

    @Override
    protected List<String> getText() {
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(decimalPlaces.get().intValue());

        String x = format.format(Minecraft.getInstance().player.getX());
        String y = format.format(Minecraft.getInstance().player.getY());
        String z = format.format(Minecraft.getInstance().player.getZ());

        if(spaced.get()) {
            x = I18n.get("betterHud.hud.x", x);
            y = I18n.get("betterHud.hud.y", y);
            z = I18n.get("betterHud.hud.z", z);
            return Arrays.asList(x, y, z);
        } else {
            return Arrays.asList(I18n.get("betterHud.hud.xyz", x, y, z));
        }
    }
}
