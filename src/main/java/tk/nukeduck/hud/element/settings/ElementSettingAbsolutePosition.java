package tk.nukeduck.hud.element.settings;

import java.io.IOException;
import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.gui.GuiUpDownButton;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.constants.Constants;

public class ElementSettingAbsolutePosition extends ElementSetting {
	public GuiTextField xBox, yBox;
	public GuiButton pick;
	private GuiButton xUp, xDown, yUp, yDown;
	
	public int x = 0;
	public int y = 0;
	
	public ElementSettingAbsolutePosition(String name) {
		super(name);
	}
	
	@Override
	public int getGuiHeight() {
		return 42;
	}
	
	@Override
	public Gui[] getGuiParts(int width, int y) {
		this.xBox = new GuiTextField(0, Minecraft.getMinecraft().fontRendererObj, width / 2 - 106, y + 1, 80, 18);
		xBox.setText(String.valueOf(this.x));
		this.yBox = new GuiTextField(0, Minecraft.getMinecraft().fontRendererObj, width / 2 + 2, y + 1, 80, 18);
		yBox.setText(String.valueOf(this.y));
		xUp = new GuiUpDownButton(0, width / 2 - 22, y, 0);
		xDown = new GuiUpDownButton(1, width / 2 - 22, y + 10, 1);
		yUp = new GuiUpDownButton(2, width / 2 + 86, y, 0);
		yDown = new GuiUpDownButton(3, width / 2 + 86, y + 10, 1);
		
		pick = new GuiButton(4, width / 2 - 75, y + 22, 150, 20, FormatUtil.translatePre("menu.pick"));
		
		return new Gui[] {xBox, xUp, xDown, yUp, yDown, yBox, pick};
	}
	
	public boolean isPicking = false;
	public int xRestore, yRestore;
	
	public void updateText() {updateText(x, y);}
	public void updateText(int x, int y) {
		xBox.setText(String.valueOf(x));
		yBox.setText(String.valueOf(y));
	}
	
	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		switch(button.id) {
			case 0:
				xBox.setText(String.valueOf(++x));
				break;
			case 1:
				xBox.setText(String.valueOf(--x));
				break;
			case 2:
				yBox.setText(String.valueOf(++y));
				break;
			case 3:
				yBox.setText(String.valueOf(--y));
				break;
			case 4:
				isPicking = !isPicking;
				if(isPicking) {
					xRestore = x;
					yRestore = y;
				} else {
					x = xRestore;
					y = yRestore;
				}
				
				gui.currentPicking = isPicking ? this : null;
				button.displayString = FormatUtil.translatePre("menu.pick" + (isPicking ? "ing" : ""));
				updateText();
				break;
		}
	}
	
	@Override
	public void keyTyped(char typedChar, int keyCode) throws IOException {
		if(!pick.enabled) return;
		
		xUp.enabled = true;
		xDown.enabled = true;
		try {
			this.x = Integer.parseInt(xBox.getText());
		} catch(NumberFormatException e) {
			this.x = 0;
			xUp.enabled = false;
			xDown.enabled = false;
		}
		yUp.enabled = true;
		yDown.enabled = true;
		try {
			this.y = Integer.parseInt(yBox.getText());
		} catch(NumberFormatException e) {
			this.y = 0;
			yUp.enabled = false;
			yDown.enabled = false;
		}
	}
	
	@Override
	public void render(GuiScreen gui, int yScroll) {}
	
	@Override
	public String toString() {
		return String.valueOf(this.x) + Constants.VALUE_SEPARATOR + String.valueOf(this.y);
	}
	
	@Override
	public void fromString(String val) {
		if(val.contains(Constants.VALUE_SEPARATOR)) {
			String[] xy = val.split(Constants.VALUE_SEPARATOR);
			if(xy.length >= 2) {
				try {
					this.x = Integer.parseInt(xy[0]);
					this.y = Integer.parseInt(xy[1]);
				} catch(NumberFormatException e) {}
			}
		}
	}
	
	@Override
	public void otherAction(Collection<ElementSetting> settings) {
		boolean enabled = this.getEnabled();
		xBox.setEnabled(enabled);
		yBox.setEnabled(enabled);
		pick.enabled = enabled;
		xUp.enabled = enabled;
		xDown.enabled = enabled;
		yUp.enabled = enabled;
		yDown.enabled = enabled;
	}
}