package jobicade.betterhud.element.settings;

import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.gui.GuiOffsetChooser;
import jobicade.betterhud.gui.GuiUpDownButton;
import net.minecraft.client.gui.AbstractGui;
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

    public SettingAbsolutePosition(String name) {
        this(name, null);
    }

    public SettingAbsolutePosition(String name, SettingPosition position) {
        super(name);
        this.position = position;
    }

    @Override
    public Point getGuiParts(List<AbstractGui> parts, Map<AbstractGui, Setting> callbacks, Point origin) {
        parts.add(xBox = new TextFieldWidget(MC.fontRenderer, origin.getX() - 106, origin.getY() + 1, 80, 18, ""));
        xBox.setText(String.valueOf(x));
        parts.add(yBox = new TextFieldWidget(MC.fontRenderer, origin.getX() + 2, origin.getY() + 1, 80, 18, ""));
        yBox.setText(String.valueOf(y));

        parts.add(xUp   = new GuiUpDownButton(true ).setBounds(new Rect(origin.getX() - 22, origin.getY(),      0, 0)).setId(0).setRepeat());
        parts.add(xDown = new GuiUpDownButton(false).setBounds(new Rect(origin.getX() - 22, origin.getY() + 10, 0, 0)).setId(1).setRepeat());
        parts.add(yUp   = new GuiUpDownButton(true ).setBounds(new Rect(origin.getX() + 86, origin.getY(),      0, 0)).setId(2).setRepeat());
        parts.add(yDown = new GuiUpDownButton(false).setBounds(new Rect(origin.getX() + 86, origin.getY() + 10, 0, 0)).setId(3).setRepeat());

        if(position != null) {
            parts.add(pick = new Button(origin.getX() - 100, origin.getY() + 22, 200, 20, I18n.format("betterHud.menu.pick")));
            callbacks.put(pick, this);
        }

        callbacks.put(xBox, this);
        callbacks.put(yBox, this);
        callbacks.put(xUp, this);
        callbacks.put(xDown, this);
        callbacks.put(yUp, this);
        callbacks.put(yDown, this);

        return origin.add(0, 42 + SPACER);
    }

    public void updateText() {
        if(xBox != null && yBox != null) {
            xBox.setText(String.valueOf(x));
            yBox.setText(String.valueOf(y));
        }
    }

    @Override
    public void actionPerformed(GuiElementSettings gui, Button button) {
        switch(button.id) {
            case 0: xBox.setText(String.valueOf(++x)); break;
            case 1: xBox.setText(String.valueOf(--x)); break;
            case 2: yBox.setText(String.valueOf(++y)); break;
            case 3: yBox.setText(String.valueOf(--y)); break;
            case 4: MC.displayGuiScreen(new GuiOffsetChooser(gui, position)); break;
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
