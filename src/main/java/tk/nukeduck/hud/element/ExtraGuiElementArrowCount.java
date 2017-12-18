package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import tk.nukeduck.hud.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ExtraGuiElementArrowCount extends ExtraGuiElement {
	public ExtraGuiElementArrowCount() {
		name = "arrowCount";
		modes = new String[] {"left", "right"};
		defaultMode = 1;
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		if(mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem().equals(Items.bow)) {
			int arrows = 0;
			ItemStack[] inventory = mc.thePlayer.inventory.mainInventory;
			for(ItemStack i : inventory) {
				if(i != null && i.getItem().equals(Items.arrow)) {
					arrows += i.stackSize;
				}
			}
			
			if(currentMode() == "left") {
				RenderUtil.renderItem(ri, fr, mc.getTextureManager(), new ItemStack(Items.arrow, 1), halfWidth - 111, height - 18);
				mc.ingameGUI.drawString(fr, arrows + "", halfWidth - 111, height - 10 - fr.FONT_HEIGHT, 0xffffff);
			} else {
				RenderUtil.renderItem(ri, fr, mc.getTextureManager(), new ItemStack(Items.arrow, 1), halfWidth + 95, height - 18);
				mc.ingameGUI.drawString(fr, arrows + "", halfWidth + 95, height - 10 - fr.FONT_HEIGHT, 0xffffff);
			}
		}
	}
}