package tk.nukeduck.hud.element.settings;

import static tk.nukeduck.hud.BetterHud.MC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.util.Colors;

public class Legend extends Setting {
	public Legend(String name) {
		super(name);
	}

	@Override
	public int getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, int width, int y) {
		GuiLegendLabel label = new GuiLegendLabel(0, width / 2 - 150, y, 300, MC.fontRenderer.FONT_HEIGHT, Colors.WHITE);
		label.addLine("betterHud.group." + this.name);
		parts.add(label);

		return y + MC.fontRenderer.FONT_HEIGHT;
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
			lines.add(line);
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

	@Override public void actionPerformed(GuiElementSettings gui, GuiButton button) {}
	@Override public void keyTyped(char typedChar, int keyCode) throws IOException {}
	@Override public String save() {return null;}
	@Override public void load(String val) {}
	@Override public void otherAction(Collection<Setting> settings) {}
}
