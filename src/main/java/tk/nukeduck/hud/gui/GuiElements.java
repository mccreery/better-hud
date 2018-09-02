package tk.nukeduck.hud.gui;

import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.client.gui.GuiScreen;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.GlUtil;

public abstract class GuiElements extends GuiScreen {
	protected static HudElement getHoveredElement(int mouseX, int mouseY, Predicate<HudElement> ignore) {
		Map.Entry<HudElement, Bounds> result = null;

		for(Map.Entry<HudElement, Bounds> entry : HudElement.getActiveBounds().entrySet()) {
			if(!ignore.test(entry.getKey())) {
				Bounds bounds = entry.getValue();

				if(bounds.contains(mouseX, mouseY) && (result == null ||
						bounds.getWidth() < result.getValue().getWidth() &&
						bounds.getHeight() < result.getValue().getHeight())) {
					result = entry;
				}
			}
		}
		return result != null ? result.getKey() : null;
	}

	private static final int FADED = Colors.setAlpha(Colors.RED, 63);
	private static final int HIGHLIGHT = Colors.RED;

	protected static void drawBounds(Bounds bounds, boolean highlight) {
		GlUtil.drawBorderRect(bounds, highlight ? HIGHLIGHT : FADED);
	}
}
