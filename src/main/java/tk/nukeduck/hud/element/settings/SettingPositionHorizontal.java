package tk.nukeduck.hud.element.settings;

import net.minecraft.client.gui.Gui;
import tk.nukeduck.hud.gui.GuiToggleButton;

public class SettingPositionHorizontal extends SettingPosition {
	public SettingPositionHorizontal(String name, int possibleLocations) {
		super(name, possibleLocations);
		this.possibleLocations = possibleLocations & Position.combine(Position.MIDDLE_LEFT, Position.MIDDLE_CENTER, Position.MIDDLE_RIGHT);
	}
	
	@Override
	public int getGuiHeight() {
		return 20;
	}
	
	@Override
	public Gui[] getGuiParts(int width, int y) {
		middleLeft = new GuiToggleButton(Position.MIDDLE_LEFT.ordinal(), width / 2 - 100, y, 20, 20, "");
		middleCenter = new GuiToggleButton(Position.MIDDLE_CENTER.ordinal(), width / 2 - 78, y, 20, 20, "");
		middleRight = new GuiToggleButton(Position.MIDDLE_RIGHT.ordinal(), width / 2 - 56, y, 20, 20, "");
		
		radios = new GuiToggleButton[] {
			middleLeft, middleCenter, middleRight
		};
		for(GuiToggleButton but : radios) {
			but.enabled = (Position.values()[but.id].getFlag() & this.possibleLocations) == Position.values()[but.id].getFlag();
			but.pressed = but.id == this.value.ordinal();
		}
		return radios;
	}
}