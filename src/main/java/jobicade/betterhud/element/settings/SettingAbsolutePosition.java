package jobicade.betterhud.element.settings;

import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import java.util.Collection;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.gui.GuiOffsetChooser;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;

public class SettingAbsolutePosition extends Setting {
    public TextFieldWidget xBox, yBox;
    public Button pick;
    private Button xUp, xDown, yUp, yDown;

    private final SettingPosition position;

    protected int x, y, cancelX, cancelY;
    protected boolean isPicking = false;

    public boolean isPicking() {
        return isPicking;
    }

    public SettingAbsolutePosition(HudElement<?> element, String name) {
        super(element, name);
        this.position = null;
    }

    public SettingAbsolutePosition(Setting parent, String name) {
        super(parent, name);
        this.position = null;
    }

    public SettingAbsolutePosition(SettingPosition parent, String name) {
        super(parent, name);
        this.position = parent;
    }

    @Override
    public Point getGuiParts(GuiElementSettings.Populator populator, Point origin) {
        populator.add(xBox = new TextFieldWidget(MC.fontRenderer, origin.getX() - 106, origin.getY() + 1, 80, 18, ""));
        xBox.setText(String.valueOf(x));
        populator.add(yBox = new TextFieldWidget(MC.fontRenderer, origin.getX() + 2, origin.getY() + 1, 80, 18, ""));
        yBox.setText(String.valueOf(y));

        xUp = populator.add(new Button(origin.getX() - 22, origin.getY(), 20, 10, "", b -> xBox.setText(String.valueOf(++x))));
        xDown = populator.add(new Button(origin.getX() - 22, origin.getY() + 10, 20, 10, "", b -> xBox.setText(String.valueOf(--x))));
        yUp = populator.add(new Button(origin.getX() - 86, origin.getY(), 20, 10, "", b -> yBox.setText(String.valueOf(++y))));
        yDown = populator.add(new Button(origin.getX() - 86, origin.getY() + 10, 20, 10, "", b -> yBox.setText(String.valueOf(--y))));

        if(position != null) {
            pick = populator.add(new Button(origin.getX() - 100, origin.getY() + 22, 200, 20, I18n.format("betterHud.menu.pick"), b -> pick()));
        }

        return origin.add(0, 42 + SPACER);
    }

    private void pick() {
        GuiElementSettings gui = (GuiElementSettings)MC.currentScreen;
        MC.displayGuiScreen(new GuiOffsetChooser(gui, position));
    }

    public void updateText() {
        if(xBox != null && yBox != null) {
            xBox.setText(String.valueOf(x));
            yBox.setText(String.valueOf(y));
        }
    }

    /** Forgets the original position and keeps the current picked position */
    public void finishPicking() {
        isPicking = false;
        //pick.displayString = I18n.format("betterHud.menu.pick");
    }

    public void set(Point value) {
        x = value.getX();
        y = value.getY();
        updateText();
    }

    public Point get() {
        return new Point(x, y);
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public String getStringValue() {
        return x + ", " + y;
    }

    @Override
    public void loadStringValue(String val) {
        int comma = val.indexOf(',');

        if (comma == -1) {
            //return false;
        }

        int x, y;
        try {
            x = Integer.parseInt(val.substring(0, comma).trim());
            y = Integer.parseInt(val.substring(comma + 1).trim());
        } catch (NumberFormatException e) {
            //return false;
            return;
        }

        set(new Point(x, y));
    }

    @Override
    public void updateGuiParts(Collection<Setting> settings) {
        super.updateGuiParts(settings);

        boolean enabled = enabled();
        xBox.setEnabled(enabled);
        yBox.setEnabled(enabled);

        if(pick != null) pick.active = enabled;

        if(enabled) {
            try {
                x = Integer.parseInt(xBox.getText());
                xUp.active = xDown.active = true;
            } catch(NumberFormatException e) {
                x = 0;
                xUp.active = xDown.active = false;
            }

            try {
                y = Integer.parseInt(yBox.getText());
                yUp.active = yDown.active = true;
            } catch(NumberFormatException e) {
                y = 0;
                yUp.active = yDown.active = false;
            }
        } else {
            xUp.active = xDown.active = yUp.active = yDown.active = false;
        }
    }
}
