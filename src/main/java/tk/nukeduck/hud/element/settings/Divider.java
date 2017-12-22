package tk.nukeduck.hud.element.settings;

import java.io.IOException;
import java.util.Collection;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.util.constants.Colors;

public class Divider extends Setting {
	public Divider(String name) {
		super(name);
	}
	
	int y = 0;
	
	@Override
	public Gui[] getGuiParts(int width, int y) {
		this.y = y;
		return new Gui[] {};
	}

	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {}

	@Override
	public void keyTyped(char typedChar, int keyCode) throws IOException {}

	@Override
	public void render(GuiScreen gui, int yScroll) {
		String text = I18n.format("betterHud.group." + this.getName());
		int textWidth = gui.mc.fontRenderer.getStringWidth(text);
		Gui.drawRect(gui.width / 2 - 150, y + 5 - yScroll, (gui.width - textWidth) / 2 - 5, y + 6 - yScroll, Colors.WHITE);
		Gui.drawRect((gui.width + textWidth) / 2 + 5, y + 5 - yScroll, gui.width / 2 + 150, y + 6 - yScroll, Colors.WHITE);
		gui.drawCenteredString(gui.mc.fontRenderer, text, gui.width / 2, y + 1 - yScroll, Colors.WHITE);
	}
	
	@Override
	public int getGuiHeight() {
		return 10;
	}

	@Override
	public String toString() {
		return null;
	}
	@Override
	public void fromString(String val) {}

	@Override
	public void otherAction(Collection<Setting> settings) {}
}
