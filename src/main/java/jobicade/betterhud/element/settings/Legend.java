package jobicade.betterhud.element.settings;

import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.render.Color;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;

public class Legend extends Setting {
    public Legend(String name) {
        super(name);
    }

    @Override
    public JsonElement saveJson(Gson gson) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void loadJson(Gson gson, JsonElement element) throws JsonSyntaxException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Point getGuiParts(GuiElementSettings.Populator populator, Point origin) {
        String message = I18n.format("betterHud.group." + getName());
        populator.add(new LegendLabel(origin.getX() - 150, origin.getY(), 300, MC.fontRenderer.FONT_HEIGHT, message, Color.WHITE));

        return origin.add(0, MC.fontRenderer.FONT_HEIGHT + SPACER);
    }

    private static class LegendLabel extends Widget {
        protected final Color color;

        public LegendLabel(int x, int y, int width, int height, String message, Color color) {
            super(x, y, width, height, message);
            this.color = color;
            active = false;
        }

        @Override
        public void render(int mouseX, int mouseY, float partialTicks) {
            if (visible) {
                int blank = (MC.fontRenderer.getStringWidth(getMessage()) + BetterHud.SPACER * 2) / 2;

                int cx = x + width / 2;
                int cy = y + height / 2;

                this.drawCenteredString(MC.fontRenderer, getMessage(), cx, y, color.getPacked());
                fill(x, cy, cx - blank, cy + 1, color.getPacked());
                fill(cx + blank, cy, x + width, cy + 1, color.getPacked());
            }
        }
    }
}
