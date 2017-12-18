package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
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
			
			mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
			//mc.getTextureManager().bindTexture(ExtraGuiElementBlood.blood);
			if(currentMode() == "left") {
				//BetterHud.itemRendererGui.drawTexturedModalRect(halfWidth - 111, height - 18, 80, 64, 16, 16);
				RenderUtil.renderItem(ri, fr, mc.getTextureManager(), arrow, halfWidth - 111, height - 18);
				mc.ingameGUI.drawString(fr, arrows + "", halfWidth - 111, height - 10 - fr.FONT_HEIGHT, 0xffffff);
			} else {
				//BetterHud.itemRendererGui.drawTexturedModalRect(halfWidth + 95, height - 18, 80, 64, 16, 16);
				RenderUtil.renderItem(ri, fr, mc.getTextureManager(), arrow, halfWidth + 95, height - 18);
				mc.ingameGUI.drawString(fr, arrows + "", halfWidth + 95, height - 10 - fr.FONT_HEIGHT, 0xffffff);
			}
		}
	}
	
	private static final ItemStack arrow = new ItemStack(Items.arrow, 1);
}