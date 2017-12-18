package tk.nukeduck.hud.element;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import tk.nukeduck.hud.util.RenderUtil;

public class ExtraGuiElementHandBar extends ExtraGuiElement {
	public ExtraGuiElementHandBar() {
		name = "handBar";
		modes = new String[] {"default", "handBar.barOnly", "armorBars.numbersOnly"};
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		TextureManager tm = mc.getTextureManager();
		ItemStack item = mc.thePlayer.getCurrentEquippedItem();
		if(item != null && item.getMaxDamage() > 0) {
			float value = (float) (item.getMaxDamage() - item.getItemDamage()) / (float) item.getMaxDamage();
			byte green = (byte) (255 * value);
			byte red = (byte) (256 - green);
			
			String text = (!currentMode().equals("armorBars.numbersOnly") ? item.getDisplayName() + " - " : "") + (item.getMaxDamage() - item.getItemDamage()) + "/" + item.getMaxDamage();
			
			if(!currentMode().equals("handBar.barOnly")) {
				mc.mcProfiler.startSection("items");
				RenderUtil.renderItem(ri, fr, tm, item, halfWidth - (fr.getStringWidth(text) / 2) - 16, height - 84);
				mc.mcProfiler.endSection();
				
				mc.mcProfiler.startSection("text");
				mc.ingameGUI.drawString(fr, text, halfWidth - (fr.getStringWidth(text) / 2) + 8, height - 80, 0xffffff);
				mc.mcProfiler.endSection();
			}
			
			mc.mcProfiler.startSection("bars");
			mc.ingameGUI.drawRect(halfWidth - 90, height - 68, halfWidth + 90, height - 66, 0xff000000);
			mc.ingameGUI.drawRect(halfWidth - 90, height - 68, Math.round(halfWidth - 90 + (value * 180)), height - 67, ExtraGuiElementArmorBars.colorARGB(255, red, green, 0));
			mc.mcProfiler.endSection();
		}
	}
}