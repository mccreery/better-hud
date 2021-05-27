package jobicade.betterhud.gui;

import jobicade.betterhud.geom.Rect;

public class GuiUpDownButton extends GuiTexturedButton {
    public GuiUpDownButton(boolean up) {
        super(new Rect(0, up ? 0 : 10, 20, 10), 20);
    }
}
