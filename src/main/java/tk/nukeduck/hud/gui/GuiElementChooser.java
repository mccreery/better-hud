package tk.nukeduck.hud.gui;

import static tk.nukeduck.hud.BetterHud.MC;

import java.io.IOException;
import java.util.Map;

import net.minecraft.client.gui.GuiScreen;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.Setting;
import tk.nukeduck.hud.util.Bounds;

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
			MC.displayGuiScreen(parent);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		MC.displayGuiScreen(parent);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		HudElement selected = getHoveredElement(mouseX, mouseY, (HudElement element) -> {
			return element != this.element;
		});
		setting.set(selected);

		for(Map.Entry<HudElement, Bounds> entry : HudElement.getActiveBounds().entrySet()) {
			drawBounds(entry.getValue(), entry.getKey() == selected);
		}
	}
}
