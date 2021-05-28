package jobicade.betterhud.element.settings;

import jobicade.betterhud.geom.Point;
import jobicade.betterhud.render.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static jobicade.betterhud.BetterHud.SPACER;

public class Legend extends SettingStub<Object> {
    public Legend(String name) {
        super(name);
    }

    @Override
    protected boolean hasValue() {
        return false;
    }

    @Override
    public Point getGuiParts(List<AbstractGui> parts, Map<AbstractGui, Setting<?>> callbacks, Point origin) {
        GuiLegendLabel label = new GuiLegendLabel(0, origin.getX() - 150, origin.getY(), 300, Minecraft.getInstance().font.lineHeight, Color.WHITE);
        label.func_175202_a("betterHud.group." + this.name);
        parts.add(label);

        return origin.add(0, Minecraft.getInstance().font.lineHeight + SPACER);
    }

    private static class GuiLegendLabel extends GuiLabel {
        protected final Color color;
        protected final List<String> lines = new ArrayList<String>();

        public GuiLegendLabel(int id, int x, int y, int width, int height, Color color) {
            super(Minecraft.getInstance().font, id, x, y, width, height, color.getPacked());
            func_175203_a();

            this.color = color;
        }

        @Override
        public void func_175202_a(String line) {
            super.func_175202_a(line);
            lines.add(I18n.get(line));
        }

        private int getMaxWidth(Collection<String> lines) {
            int maxWidth = 0;

            for(String s : lines) {
                int width = Minecraft.getInstance().font.width(s);
                if(width > maxWidth) maxWidth = width;
            }

            return maxWidth;
        }

        @Override
        public void func_146159_a(Minecraft mc, int mouseX, int mouseY) {
            super.func_146159_a(mc, mouseX, mouseY);

            if(field_146172_j) {
                int blank = getMaxWidth(lines) / 2 + 5;
                int top = field_146174_h + field_146161_f / 2;
                int center = field_146162_g + field_146167_a / 2;

                func_73734_a(field_146162_g, top, center - blank, top + 1, color.getPacked());
                func_73734_a(center + blank, top, field_146162_g + field_146167_a, top + 1, color.getPacked());
            }
        }
    }
}
