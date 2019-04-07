package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.MC;

import org.lwjgl.glfw.GLFW;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.geom.Rect;
import net.minecraft.client.gui.GuiScreen;

public class GuiElementChooser extends GuiElements {
	private final GuiScreen parent;
	private final HudElement element;

	private final Setting<HudElement> setting;

	public GuiElementChooser(GuiScreen parent, HudElement element, Setting<HudElement> setting) {
		this.parent = parent;
		this.element = element;
		this.setting = setting;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if(keyCode == GLFW.GLFW_KEY_ESCAPE) {
			setting.set(null);
			MC.displayGuiScreen(parent);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int modifiers) {
		MC.displayGuiScreen(parent);
		return true;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		HudElement selected = getHoveredElement(mouseX, mouseY, (HudElement element) -> {
			return element == this.element;
		});
		setting.set(selected);

		for(HudElement element : HudElement.ELEMENTS) {
			Rect bounds = element.getLastBounds();

			if(!bounds.isEmpty()) {
				drawRect(bounds, element == selected);
			}
		}
	}
}
