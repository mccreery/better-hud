package tk.nukeduck.hud.gui;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;

public class GuiOffsetChooser extends GuiScreen {
	private final GuiElementSettings parent;
	private final SettingPosition setting;

	public GuiOffsetChooser(GuiElementSettings parent, SettingPosition setting) {
		this.parent = parent;
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
		Point anchor = setting.getParent().getAnchor(setting.getAnchor());
		Point offset = new Point(mouseX, mouseY).sub(anchor);

		if(!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			int x = (offset.getX() + SPACER * 3 / 2) / SPACER - 1;
			if(x >= -1 && x <= 1) {
				offset = offset.withX(x * SPACER);
			}
			
			int y = (offset.getY() + SPACER * 3 / 2) / SPACER - 1;
			if(y >= -1 && y <= 1) {
				offset = offset.withY(y * SPACER);
			}
		}
		setting.setOffset(offset);

		GlUtil.drawBorderRect(setting.getParent(), Colors.setAlpha(Colors.RED, 63));

		Bounds elementBounds = parent.element.getLastBounds();
		if(!elementBounds.isEmpty()) {
			GlUtil.drawBorderRect(parent.element.getLastBounds(), Colors.RED);
		} else {
			Point mouse = offset.add(anchor);
			drawHorizontalLine(mouse.getX() - SPACER, mouse.getX() + SPACER, mouse.getY(), Colors.RED);
			drawVerticalLine(mouse.getX(), mouse.getY() - SPACER, mouse.getY() + SPACER, Colors.RED);
		}

		String key = Keyboard.getKeyName(Keyboard.KEY_LCONTROL);
		drawString(fontRenderer, I18n.format("betterHud.menu.unsnap", key), SPACER, SPACER, Colors.WHITE);

		drawHoveringText(offset.toString(), mouseX, mouseY);
	}
}
