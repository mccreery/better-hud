package jobicade.betterhud.element.settings;

import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jobicade.betterhud.geom.Point;
import jobicade.betterhud.render.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.resources.I18n;

public class Legend extends SettingStub {
    public Legend(String name) {
        super(name);
    }

    @Override
    public boolean hasValue() {
        return false;
    }

    @Override
    public Point getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Point origin) {
        GuiLegendLabel label = new GuiLegendLabel(0, origin.getX() - 150, origin.getY(), 300, MC.fontRenderer.FONT_HEIGHT, Color.WHITE);
        label.addLine("betterHud.group." + this.name);
        parts.add(label);

        return origin.add(0, MC.fontRenderer.FONT_HEIGHT + SPACER);
    }

    private static class GuiLegendLabel extends GuiLabel {
        protected final Color color;
        protected final List<String> lines = new ArrayList<String>();

        public GuiLegendLabel(int id, int x, int y, int width, int height, Color color) {
            super(MC.fontRenderer, id, x, y, width, height, color.getPacked());
            setCentered();

            this.color = color;
        }

        @Override
        public void addLine(String line) {
            super.addLine(line);
            lines.add(I18n.format(line));
        }

        private int getMaxWidth(Collection<String> lines) {
            int maxWidth = 0;

            for(String s : lines) {
                int width = MC.fontRenderer.getStringWidth(s);
                if(width > maxWidth) maxWidth = width;
            }

            return maxWidth;
        }

        @Override
        public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
            super.drawLabel(mc, mouseX, mouseY);

            if(visible) {
                int blank = getMaxWidth(lines) / 2 + 5;
                int top = y + height / 2;
                int center = x + width / 2;

                drawRect(x, top, center - blank, top + 1, color.getPacked());
                drawRect(center + blank, top, x + width, top + 1, color.getPacked());
            }
        }
    }
}
