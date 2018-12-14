package jobicade.betterhud.gui;

import jobicade.betterhud.util.Bounds;

public class GuiUpDownButton extends GuiTexturedButton {
	public GuiUpDownButton(boolean up) {
		super(new Bounds(0, up ? 0 : 10, 20, 10), 20);
	}
}
