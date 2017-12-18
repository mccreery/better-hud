package tk.nukeduck.hud.element;

import java.util.ArrayList;

import tk.nukeduck.hud.BetterHud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;

public class ExtraGuiElementHealIndicator extends ExtraGuiElement {
	public ExtraGuiElementHealIndicator() {
		name = "healIndicator";
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		if(mc.thePlayer.getFoodStats().getFoodLevel() >= 18 && mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth()) {
			String hi = I18n.format("betterHud.strings.healIndicator", new Object[0]);
			mc.ingameGUI.drawString(fr, hi, halfWidth - 100 - fr.getStringWidth(hi + (BetterHud.getFromName(BetterHud.elements, "foodHealthStats").enabled ? ((float) Math.ceil(mc.thePlayer.getHealth()) / 2) : "")), height - 35, 0x00ff00);
		}
	}
}