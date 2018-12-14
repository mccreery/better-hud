package jobicade.betterhud.gui;

import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.client.gui.GuiScreen;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.util.geom.Rect;
import jobicade.betterhud.util.Colors;
import jobicade.betterhud.util.GlUtil;

public abstract class GuiElements extends GuiScreen {
	protected static HudElement getHoveredElement(int mouseX, int mouseY, Predicate<HudElement> ignore) {
		Map.Entry<HudElement, Rect> result = null;

		for(Map.Entry<HudElement, Rect> entry : HudElement.getActiveRect().entrySet()) {
			if(!ignore.test(entry.getKey())) {
				Rect bounds = entry.getValue();

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

	protected static void drawRect(Rect bounds, boolean highlight) {
		GlUtil.drawBorderRect(bounds, highlight ? HIGHLIGHT : FADED);
	}
}
