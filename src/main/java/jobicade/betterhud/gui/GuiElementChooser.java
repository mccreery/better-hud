package jobicade.betterhud.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.geom.Rect;

public class GuiElementChooser extends GuiElements {
	private final GuiScreen parent;
	private final HudElement element;

	private final Setting<HudElement> setting;

	public GuiElementChooser(GuiScreen parent, HudElement element, Setting<HudElement> setting) {
		this.parent = parent;
		this.element = element;
		this.setting = setting;
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(keyCode == 1) {
			setting.set(null);
			Minecraft.getMinecraft().displayGuiScreen(parent);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		Minecraft.getMinecraft().displayGuiScreen(parent);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		HudElement selected = getHoveredElement(mouseX, mouseY, (HudElement element) -> {
			return element == this.element;
		});
		setting.set(selected);

		for(HudElement element : HudElement.ELEMENTS) {
			Rect bounds = element.getLastBounds();

			if(!bounds.isEmpty()) {
				drawRect(bounds, element == selected);
			}
		}
	}
}
