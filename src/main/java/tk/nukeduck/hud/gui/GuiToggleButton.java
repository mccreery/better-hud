package tk.nukeduck.hud.gui;

import net.minecraft.client.gui.GuiButton;

public class GuiToggleButton extends GuiButton {
	public GuiToggleButton(int buttonId, int x, int y, String buttonText) {
		super(buttonId, x, y, buttonText);
	}
	
	public GuiToggleButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
	}
	
	public boolean pressed = false;
	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}
	public boolean toggle() {
		setPressed(!pressed);
		return pressed;
	}
	
    @Override
    protected int getHoverState(boolean mouseOver) {
        if(pressed && enabled) return 2;
        return super.getHoverState(mouseOver);
    }
}
