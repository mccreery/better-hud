package tk.nukeduck.hud.element;

import java.util.ArrayList;

import tk.nukeduck.hud.BetterHud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;

public class ExtraGuiElementBiome extends ExtraGuiElement {
	public ExtraGuiElementBiome() {
		name = "biome";
		modes = new String[] {"left", "middle", "right"};
		defaultMode = 1;
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		int y = 17 + (BetterHud.getFromName(BetterHud.elements, "compass").enabled ? 23 : 0);
		String m = currentMode();
		String b = "Biome: " + mc.theWorld.getBiomeGenForCoords((int) mc.thePlayer.posX, (int) mc.thePlayer.posZ).biomeName;
		if(m.equals("left")) {
			leftStrings.add(b);
		} else if(m.equals("right")) {
			rightStrings.add(b);
		} else {
			mc.ingameGUI.drawCenteredString(fr, b, halfWidth, y, 0xffffff);
		}
	}
}