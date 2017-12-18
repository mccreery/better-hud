package tk.nukeduck.hud.element;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;

public class ExtraGuiElementFps extends ExtraGuiElement {
	public ExtraGuiElementFps() {
		name = "fpsCount";
		modes = new String[] {"left", "right"};
		defaultMode = 1;
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		if(currentMode().equals("right")) {
			rightStrings.add(I18n.format("betterHud.strings.fps", new Object[0]).replace("*", mc.debug.split(" ")[0]));
		} else {
			leftStrings.add(I18n.format("betterHud.strings.fps", new Object[0]).replace("*", mc.debug.split(" ")[0]));
		}
	}
}