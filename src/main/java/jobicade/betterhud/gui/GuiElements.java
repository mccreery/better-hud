package jobicade.betterhud.gui;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Predicate;

public abstract class GuiElements extends Screen {
    protected static HudElement getHoveredElement(int mouseX, int mouseY, Predicate<HudElement> ignore) {
        HudElement result = null;

        for(HudElement element : HudElement.ELEMENTS) {
            if(!ignore.test(element)) {
                Rect bounds = element.getLastBounds();

                if(bounds.contains(mouseX, mouseY) && (result == null ||
                        bounds.getWidth() < result.getLastBounds().getWidth() &&
                        bounds.getHeight() < result.getLastBounds().getHeight())) {
                    result = element;
                }
            }
        }
        return result;
    }

    private static final Color FADED = Color.RED.withAlpha(63);
    private static final Color HIGHLIGHT = Color.RED;

    protected static void drawRect(Rect bounds, boolean highlight) {
        GlUtil.drawBorderRect(bounds, highlight ? HIGHLIGHT : FADED);
    }
}
