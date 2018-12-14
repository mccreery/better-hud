package jobicade.betterhud.gui;

import java.util.Map;
import java.util.function.Predicate;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.geom.Rect;
import jobicade.betterhud.render.Color;
import net.minecraft.client.gui.GuiScreen;

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

	private static final Color FADED = Color.RED.withAlpha(63);
	private static final Color HIGHLIGHT = Color.RED;

	protected static void drawRect(Rect bounds, boolean highlight) {
		GlUtil.drawBorderRect(bounds, highlight ? HIGHLIGHT : FADED);
	}
}
