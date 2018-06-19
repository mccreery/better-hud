package tk.nukeduck.hud.element.settings;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Point;

public class Legend extends SettingStub<Object> {
	public Legend(String name) {
		super(name);
	}

	@Override
	protected boolean hasValue() {
		return false;
	}

	@Override
	public Point getGuiParts(List<Gui> parts, Map<Gui, Setting<?>> callbacks, Point origin) {
		GuiLegendLabel label = new GuiLegendLabel(0, origin.getX() - 150, origin.getY(), 300, MC.fontRenderer.FONT_HEIGHT, Colors.WHITE);
		label.addLine("betterHud.group." + this.name);
		parts.add(label);

		return origin.add(0, MC.fontRenderer.FONT_HEIGHT + SPACER);
	}

	private static class GuiLegendLabel extends GuiLabel {
		protected final int color;
		protected final List<String> lines = new ArrayList<String>();

		public GuiLegendLabel(int id, int x, int y, int width, int height, int color) {
			super(MC.fontRenderer, id, x, y, width, height, color);
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

				drawRect(x, top, center - blank, top + 1, color);
				drawRect(center + blank, top, x + width, top + 1, color);
			}
		}
	}
}
