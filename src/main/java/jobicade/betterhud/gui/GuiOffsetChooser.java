package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import org.lwjgl.glfw.GLFW;

import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiOffsetChooser extends GuiScreen {
	private final GuiElementSettings parent;
	private final SettingPosition setting;

	public GuiOffsetChooser(GuiElementSettings parent, SettingPosition setting) {
		this.parent = parent;
		this.setting = setting;
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode) {
		if(keyCode == GLFW.GLFW_KEY_ESCAPE) {
			setting.set(null);
			MC.displayGuiScreen(parent);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		MC.displayGuiScreen(parent);
		return true;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		Point anchor = setting.getParent().getAnchor(setting.getAnchor());
		Point offset = new Point(mouseX, mouseY).sub(anchor);

		if(GLFW.glfwGetKey(MC.mainWindow.getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_RELEASE) {
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

		GlUtil.drawBorderRect(setting.getParent(), Color.RED.withAlpha(63));

		Rect elementRect = parent.element.getLastBounds();
		if(!elementRect.isEmpty()) {
			GlUtil.drawBorderRect(parent.element.getLastBounds(), Color.RED);
		} else {
			Point mouse = offset.add(anchor);
			drawHorizontalLine(mouse.getX() - SPACER, mouse.getX() + SPACER, mouse.getY(), Color.RED.getPacked());
			drawVerticalLine(mouse.getX(), mouse.getY() - SPACER, mouse.getY() + SPACER, Color.RED.getPacked());
		}

		String key = GLFW.glfwGetKeyName(GLFW.GLFW_KEY_LEFT_CONTROL, 0);
		GlUtil.drawString(I18n.format("betterHud.menu.unsnap", key), new Point(SPACER, SPACER), Direction.NORTH_WEST, Color.WHITE);

		drawHoveringText(offset.toPrettyString(), mouseX, mouseY);
	}
}
