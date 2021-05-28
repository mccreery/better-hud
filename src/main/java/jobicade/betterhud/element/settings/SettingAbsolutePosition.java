package jobicade.betterhud.element.settings;

import static jobicade.betterhud.BetterHud.SPACER;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.gui.GuiOffsetChooser;
import jobicade.betterhud.gui.GuiUpDownButton;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Point;

public class SettingAbsolutePosition extends Setting<Point> {
    public GuiTextField xBox, yBox;
    public GuiButton pick;
    private GuiButton xUp, xDown, yUp, yDown;

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
    public Point getGuiParts(List<Gui> parts, Map<Gui, Setting<?>> callbacks, Point origin) {
        parts.add(xBox = new GuiTextField(0, Minecraft.getInstance().font, origin.getX() - 106, origin.getY() + 1, 80, 18));
        xBox.setValue(String.valueOf(x));
        parts.add(yBox = new GuiTextField(0, Minecraft.getInstance().font, origin.getX() + 2, origin.getY() + 1, 80, 18));
        yBox.setValue(String.valueOf(y));

        parts.add(xUp   = new GuiUpDownButton(true ).setBounds(new Rect(origin.getX() - 22, origin.getY(),      0, 0)).setId(0).setRepeat());
        parts.add(xDown = new GuiUpDownButton(false).setBounds(new Rect(origin.getX() - 22, origin.getY() + 10, 0, 0)).setId(1).setRepeat());
        parts.add(yUp   = new GuiUpDownButton(true ).setBounds(new Rect(origin.getX() + 86, origin.getY(),      0, 0)).setId(2).setRepeat());
        parts.add(yDown = new GuiUpDownButton(false).setBounds(new Rect(origin.getX() + 86, origin.getY() + 10, 0, 0)).setId(3).setRepeat());

        if(position != null) {
            parts.add(pick = new GuiButton(4, origin.getX() - 100, origin.getY() + 22, 200, 20, I18n.get("betterHud.menu.pick")));
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
            xBox.setValue(String.valueOf(x));
            yBox.setValue(String.valueOf(y));
        }
    }

    @Override
    public void actionPerformed(GuiElementSettings gui, GuiButton button) {
        switch(button.field_146127_k) {
            case 0: xBox.setValue(String.valueOf(++x)); break;
            case 1: xBox.setValue(String.valueOf(--x)); break;
            case 2: yBox.setValue(String.valueOf(++y)); break;
            case 3: yBox.setValue(String.valueOf(--y)); break;
            case 4: Minecraft.getInstance().setScreen(new GuiOffsetChooser(gui, position)); break;
        }
    }

    /** Forgets the original position and keeps the current picked position */
    public void finishPicking() {
        isPicking = false;
        //pick.displayString = I18n.format("betterHud.menu.pick");
    }

    @Override
    public void set(Point value) {
        x = value.getX();
        y = value.getY();
        updateText();
    }

    @Override
    public Point get() {
        return new Point(x, y);
    }

    @Override
    public String save() {
        return x + ", " + y;
    }

    @Override
    public void load(String val) {
        int comma = val.indexOf(',');
        int x = Integer.parseInt(val.substring(0, comma).trim());
        int y = Integer.parseInt(val.substring(comma + 1).trim());

        set(new Point(x, y));
    }

    @Override
    public void updateGuiParts(Collection<Setting<?>> settings) {
        super.updateGuiParts(settings);

        boolean enabled = enabled();
        xBox.setEditable(enabled);
        yBox.setEditable(enabled);

        if(pick != null) pick.field_146124_l = enabled;

        if(enabled) {
            try {
                x = Integer.parseInt(xBox.getValue());
                xUp.field_146124_l = xDown.field_146124_l = true;
            } catch(NumberFormatException e) {
                x = 0;
                xUp.field_146124_l = xDown.field_146124_l = false;
            }

            try {
                y = Integer.parseInt(yBox.getValue());
                yUp.field_146124_l = yDown.field_146124_l = true;
            } catch(NumberFormatException e) {
                y = 0;
                yUp.field_146124_l = yDown.field_146124_l = false;
            }
        } else {
            xUp.field_146124_l = xDown.field_146124_l = yUp.field_146124_l = yDown.field_146124_l = false;
        }
    }
}
