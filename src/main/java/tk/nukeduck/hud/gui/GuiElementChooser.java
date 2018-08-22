package tk.nukeduck.hud.gui;

import static tk.nukeduck.hud.BetterHud.MC;

import java.io.IOException;
import java.util.Map.Entry;

import net.minecraft.client.gui.GuiScreen;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.Setting;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.GlUtil;

public class GuiElementChooser extends GuiScreen {
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
		Entry<HudElement, Bounds> result = null;
		int faded = Colors.setAlpha(Colors.RED, 63);

		for(Entry<HudElement, Bounds> entry : HudElement.getActiveBounds().entrySet()) {
			if(entry.getKey() == element) continue;
			Bounds bounds = entry.getValue();

			if(!bounds.contains(mouseX, mouseY)) {
				GlUtil.drawBorderRect(bounds, faded);
			} else if(result == null) {
				result = entry;
			} else if(bounds.getWidth() < result.getValue().getWidth() &&
					bounds.getHeight() < result.getValue().getHeight()) {
				// Swap out previous best candidate
				GlUtil.drawBorderRect(result.getValue(), faded);
				result = entry;
			}
		}

		if(result != null) {
			setting.set(result.getKey());

			GlUtil.drawBorderRect(result.getValue(), Colors.RED);
			drawHoveringText(result.getKey().getLocalizedName(), mouseX, mouseY);
		} else {
			setting.set(null);
		}
	}
}
