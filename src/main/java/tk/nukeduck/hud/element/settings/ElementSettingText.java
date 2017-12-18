package tk.nukeduck.hud.element.settings;

import java.io.IOException;
import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.RenderUtil;

public class ElementSettingText extends ElementSettingDivider {
	public ElementSettingText(String name) {
		super(name);
	}

	@Override
	public void render(GuiScreen gui, int yScroll) {
		gui.drawCenteredString(gui.mc.fontRendererObj, FormatUtil.translatePre("text." + this.getName()), gui.width / 2, y + 1 - yScroll, RenderUtil.colorRGB(255, 255, 255));
	}
	
	public int getHeight() {
		return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
	}
}