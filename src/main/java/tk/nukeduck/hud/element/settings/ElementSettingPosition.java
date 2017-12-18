package tk.nukeduck.hud.element.settings;

import java.io.IOException;
import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.gui.GuiToggleButton;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.RenderUtil;

public class ElementSettingPosition extends ElementSetting {
	public GuiToggleButton topLeft, topCenter, topRight,
		middleLeft, middleCenter, middleRight,
		bottomLeft, bottomCenter, bottomRight;
	
	public enum Position {
		TOP_LEFT("topLeft"),
		TOP_CENTER("topCenter"),
		TOP_RIGHT("topRight"),
		MIDDLE_LEFT("middleLeft"),
		MIDDLE_CENTER("middleCenter"),
		MIDDLE_RIGHT("middleRight"),
		BOTTOM_LEFT("bottomLeft"),
		BOTTOM_CENTER("bottomCenter"),
		BOTTOM_RIGHT("bottomRight");
		
		public static final int CORNERS = combine(TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT);
		
		public String name;
		Position(String name) {
			this.name = name;
		}
		
		public int getFlag() {
			return 1 << this.ordinal();
		}
		
		public static Position fromName(String name) {
			for(Position p : Position.values()) {
				if(p.name.equals(name)) return p;
			}
			return null;
		}
		
		public static int combine(Position... pos) {
			int x = 0;
			for(Position p : pos) {
				x |= p.getFlag();
			}
			return x;
		}
	}
	
	public ElementSettingPosition(String name, int possibleLocations) {
		super(name);
		this.possibleLocations = possibleLocations;
	}
	
	@Override
	public int getGuiHeight() {
		return 64;
	}
	
	public int possibleLocations = Position.TOP_LEFT.getFlag();
	public Position value = Position.TOP_LEFT;
	
	public boolean isValid(Position pos) {
		return (pos.getFlag() & possibleLocations) == pos.getFlag();
	}
	
	public void set(Position pos) {
		if(isValid(pos)) value = pos;
	}
	
	protected GuiToggleButton[] radios;
	
	@Override
	public Gui[] getGuiParts(int width, int y) {
		topLeft = new GuiToggleButton(Position.TOP_LEFT.ordinal(), width / 2 - 100, y, 20, 20, "");
		topCenter = new GuiToggleButton(Position.TOP_CENTER.ordinal(), width / 2 - 78, y, 20, 20, "");
		topRight = new GuiToggleButton(Position.TOP_RIGHT.ordinal(), width / 2 - 56, y, 20, 20, "");
		
		middleLeft = new GuiToggleButton(Position.MIDDLE_LEFT.ordinal(), width / 2 - 100, y + 22, 20, 20, "");
		middleCenter = new GuiToggleButton(Position.MIDDLE_CENTER.ordinal(), width / 2 - 78, y + 22, 20, 20, "");
		middleRight = new GuiToggleButton(Position.MIDDLE_RIGHT.ordinal(), width / 2 - 56, y + 22, 20, 20, "");
		
		bottomLeft = new GuiToggleButton(Position.BOTTOM_LEFT.ordinal(), width / 2 - 100, y + 44, 20, 20, "");
		bottomCenter = new GuiToggleButton(Position.BOTTOM_CENTER.ordinal(), width / 2 - 78, y + 44, 20, 20, "");
		bottomRight = new GuiToggleButton(Position.BOTTOM_RIGHT.ordinal(), width / 2 - 56, y + 44, 20, 20, "");
		
		radios = new GuiToggleButton[] {
			topLeft, topCenter, topRight,
			middleLeft, middleCenter, middleRight,
			bottomLeft, bottomCenter, bottomRight
		};
		for(GuiToggleButton but : radios) {
			but.enabled = (Position.values()[but.id].getFlag() & this.possibleLocations) == Position.values()[but.id].getFlag();
			but.pressed = but.id == this.value.ordinal();
		}
		return radios;
	}
	
	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		for(GuiToggleButton g : radios) {
			g.setPressed(g == button);
		}
		this.value = Position.values()[button.id];
	}
	
	@Override
	public void keyTyped(char typedChar, int keyCode) throws IOException {}
	
	@Override
	public void render(GuiScreen gui, int yScroll) {
		gui.drawString(Minecraft.getMinecraft().fontRendererObj, FormatUtil.translatePre("menu.settingButton", this.getLocalizedName(), FormatUtil.translatePre("setting." + this.value.name)), middleRight.xPosition + middleRight.width + 5, middleRight.yPosition + (middleRight.height - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT) / 2, RenderUtil.colorRGB(255, 255, 255));
	}

	@Override
	public String toString() {
		return this.value.name;
	}
	
	@Override
	public void fromString(String val) {
		Position p = Position.fromName(val);
		if(p != null) this.value = p;
	}

	@Override
	public void otherAction(Collection<ElementSetting> settings) {
		boolean enabled = this.getEnabled();
		for(GuiToggleButton but : radios) {
			but.enabled = enabled && (Position.values()[but.id].getFlag() & this.possibleLocations) == Position.values()[but.id].getFlag();
			but.pressed = Position.values()[but.id] == this.value;
		}
	}
}