package tk.nukeduck.hud.element.settings;

import java.io.IOException;
import java.util.Collection;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.RenderUtil;

public class ElementSettingDivider extends ElementSetting {
	public ElementSettingDivider(String name) {
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
		String text = FormatUtil.translatePre("group." + this.getName());
		int textWidth = gui.mc.fontRendererObj.getStringWidth(text);
		gui.drawRect(gui.width / 2 - 150, y + 5 - yScroll, (gui.width - textWidth) / 2 - 5, y + 6 - yScroll, RenderUtil.colorRGB(255, 255, 255));
		gui.drawRect((gui.width + textWidth) / 2 + 5, y + 5 - yScroll, gui.width / 2 + 150, y + 6 - yScroll, RenderUtil.colorRGB(255, 255, 255));
		gui.drawCenteredString(gui.mc.fontRendererObj, text, gui.width / 2, y + 1 - yScroll, RenderUtil.colorRGB(255, 255, 255));
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
	public void otherAction(Collection<ElementSetting> settings) {}
}