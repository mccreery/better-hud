package tk.nukeduck.hud.element.settings;

import java.io.IOException;
import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.element.settings.SettingPosition.Position;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.gui.GuiToggleButton;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.constants.Colors;

public class SettingAnchor extends Setting {
	public GuiToggleButton topLeft, topRight, bottomLeft, bottomRight;
	private GuiToggleButton[] radios;
	public int lastValue = 0, value = 0;

	public boolean enabled = true;
	@Override
	public boolean getEnabled() {
		return this.enabled;
	}

	private Position posValue;
	private void updatePosValue() {
		int anchorX = 1;
		int anchorY = 1;
		if((this.value & Position.TOP_LEFT.getFlag()) == Position.TOP_LEFT.getFlag()) {anchorX--; anchorY--;}
		if((this.value & Position.TOP_RIGHT.getFlag()) == Position.TOP_RIGHT.getFlag()) {anchorX++; anchorY--;}
		if((this.value & Position.BOTTOM_LEFT.getFlag()) == Position.BOTTOM_LEFT.getFlag()) {anchorX--; anchorY++;}
		if((this.value & Position.BOTTOM_RIGHT.getFlag()) == Position.BOTTOM_RIGHT.getFlag()) {anchorX++; anchorY++;}

		if(anchorX < 0) anchorX = 0;
		if(anchorX > 2) anchorX = 2;
		if(anchorY < 0) anchorY = 0;
		if(anchorY > 2) anchorY = 2;
		this.posValue = Position.values()[anchorY * 3 + anchorX];
	}

	public SettingAnchor(String name) {
		super(name);
	}

	/** Translates the given point using the given anchor from the old resolution
	 * to the new resolution.
	 * @param last The source resolution where the point was previously set
	 * @param next The destination resolution to set the point to
	 * @param newBounds 
	 * @param oldBounds 
	 * @return The new absolute point */
	public Point translateAnchor(Point pos, Point last, Point next, Bounds oldBounds, Bounds newBounds) {
		int anchorX = 0;
		int anchorY = 0;
		if((this.value & Position.TOP_LEFT.getFlag()) == Position.TOP_LEFT.getFlag()) {anchorX--; anchorY--;}
		if((this.value & Position.TOP_RIGHT.getFlag()) == Position.TOP_RIGHT.getFlag()) {anchorX++; anchorY--;}
		if((this.value & Position.BOTTOM_LEFT.getFlag()) == Position.BOTTOM_LEFT.getFlag()) {anchorX--; anchorY++;}
		if((this.value & Position.BOTTOM_RIGHT.getFlag()) == Position.BOTTOM_RIGHT.getFlag()) {anchorX++; anchorY++;}

		if(anchorX == 0) {
			pos.setX(pos.getX() + oldBounds.getWidth() / 2);
			pos.setX(pos.getX() * next.getX() / last.getX());
			pos.setX(pos.getX() - newBounds.getWidth() / 2);
		} else if(anchorX > 0) {
			pos.setX(pos.getX() + oldBounds.getWidth());
			pos.setX(pos.getX() - last.getX() + next.getX());
			pos.setX(pos.getX() - newBounds.getWidth());
		}
		if(anchorY == 0) {
			pos.setY(pos.getY() + oldBounds.getHeight() / 2);
			pos.setY(pos.getY() * next.getY() / last.getY());
			pos.setY(pos.getY() - newBounds.getHeight() / 2);
		} else if(anchorY > 0) {
			pos.setY(pos.getY() + oldBounds.getHeight());
			pos.setY(pos.getY() - last.getY() + next.getY());
			pos.setY(pos.getY() - newBounds.getHeight());
		}

		return pos;
	}

	public void updateValue() {
		this.lastValue = this.value;
		this.value = 0;
		for(GuiToggleButton button : this.radios) {
			if(button.pressed) this.value |= Position.values()[button.id].getFlag();
		}
		this.updatePosValue();
	}

	@Override
	public int getGuiHeight() {
		return 42;
	}

	@Override
	public Gui[] getGuiParts(int width, int y) {
		topLeft = new GuiToggleButton(Position.TOP_LEFT.ordinal(), width / 2 - 100, y, 20, 20, "");
		topRight = new GuiToggleButton(Position.TOP_RIGHT.ordinal(), width / 2 - 78, y, 20, 20, "");
		bottomLeft = new GuiToggleButton(Position.BOTTOM_LEFT.ordinal(), width / 2 - 100, y + 22, 20, 20, "");
		bottomRight = new GuiToggleButton(Position.BOTTOM_RIGHT.ordinal(), width / 2 - 78, y + 22, 20, 20, "");

		radios = new GuiToggleButton[] {
			topLeft, topRight, bottomLeft, bottomRight
		};
		for(GuiToggleButton button : radios) {
			button.pressed = (Position.values()[button.id].getFlag() & this.value) == Position.values()[button.id].getFlag();
		}
		return radios;
	}

	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		((GuiToggleButton) button).toggle();
		this.updateValue();
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) throws IOException {}

	@Override
	public void otherAction(Collection<Setting> settings) {
		boolean enabled = this.getEnabled();
		for(GuiToggleButton button : this.radios) {
			button.enabled = enabled;
		}
	}

	@Override
	public String toString() {
		return String.valueOf(this.value);
	}

	@Override
	public void fromString(String val) {
		this.value = Integer.parseInt(val);
	}

	@Override
	public void render(GuiScreen gui, int yScroll) {
		if(this.posValue == null) this.updatePosValue();
		final int x = topRight.x + topRight.width + 5;
		final int y = topRight.y + topRight.height - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2;
		final String text = I18n.format("betterHud.menu.settingButton", this.getLocalizedName(), I18n.format("betterHud.setting." + this.posValue.name));

		gui.drawString(Minecraft.getMinecraft().fontRenderer, text, x, y, Colors.WHITE);
	}
}
