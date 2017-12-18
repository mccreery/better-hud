package tk.nukeduck.hud.element;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import tk.nukeduck.hud.util.RenderUtil;

public class ExtraGuiElementFoodIndicator extends ExtraGuiElement {
	public ExtraGuiElementFoodIndicator() {
		name = "foodIndicator";
		modes = new String[] {"aboveBar", "center"};
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		if(mc.thePlayer.getFoodStats().needFood()) {
			if(currentMode().equals("center")) {
				RenderUtil.renderItem(ri, fr, mc.getTextureManager(), beef, halfWidth + 5, height / 2 + 5, Math.sin(System.currentTimeMillis() % ((mc.thePlayer.getFoodStats().getFoodLevel() + 1) * 100) / 1050.0 * Math.PI));
			} else {
				RenderUtil.renderItem(ri, fr, mc.getTextureManager(), beef, halfWidth + 75, height - 56, Math.sin(System.currentTimeMillis() % ((mc.thePlayer.getFoodStats().getFoodLevel() + 1) * 100) / 1050.0 * Math.PI));
			}
		}
	}
	
	ItemStack beef = new ItemStack(Items.cooked_beef);
}