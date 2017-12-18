package tk.nukeduck.hud.element;

import java.util.ArrayList;

import tk.nukeduck.hud.util.FormatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;

public class ExtraGuiElementFullInvIndicator extends ExtraGuiElement {
	public ExtraGuiElementFullInvIndicator() {
		name = "fullInvIndicator";
		modes = new String[] {"left", "right"};
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		for(int i = 0; i < mc.thePlayer.inventory.mainInventory.length; i++) {
			if(mc.thePlayer.inventory.mainInventory[i] == null) return;
		}
		if(currentMode().equals("left")) {
			leftStrings.add(FormatUtil.translatePre("strings.fullInv"));
		} else {
			rightStrings.add(FormatUtil.translatePre("strings.fullInv"));
		}
	}
}