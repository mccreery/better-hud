package jobicade.betterhud.element.text;

import static jobicade.betterhud.BetterHud.MANAGER;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingSlider;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;
import jobicade.betterhud.render.Grid;
import jobicade.betterhud.render.Label;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class Coordinates extends TextElement {
    private SettingBoolean spaced;
    private SettingSlider decimalPlaces;

    public Coordinates() {
        super("coordinates");

        spaced = new SettingBoolean("spaced");
        decimalPlaces = new SettingSlider("precision", 0, 5, 1);
        decimalPlaces.setUnlocalizedValue("betterHud.value.places");

        settings.addChildren(new Legend("misc"), spaced, decimalPlaces);
    }

    @Override
    public Rect render(OverlayContext context, List<String> text) {
        if(!spaced.get() || !position.isDirection(Direction.NORTH) && !position.isDirection(Direction.SOUTH)) {
            return super.render(context, text);
        }

        Grid<Label> grid = new Grid<>(new Point(3, 1), text.stream().map(Label::new).collect(Collectors.toList()))
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
        format.setMaximumFractionDigits((int)decimalPlaces.getValue());

        String x = format.format(Minecraft.getMinecraft().player.posX);
        String y = format.format(Minecraft.getMinecraft().player.posY);
        String z = format.format(Minecraft.getMinecraft().player.posZ);

        if(spaced.get()) {
            x = I18n.format("betterHud.hud.x", x);
            y = I18n.format("betterHud.hud.y", y);
            z = I18n.format("betterHud.hud.z", z);
            return Arrays.asList(x, y, z);
        } else {
            return Arrays.asList(I18n.format("betterHud.hud.xyz", x, y, z));
        }
    }
}
