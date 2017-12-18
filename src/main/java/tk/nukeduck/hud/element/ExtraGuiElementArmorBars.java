package tk.nukeduck.hud.element;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.util.RenderUtil;

public class ExtraGuiElementArmorBars extends ExtraGuiElement {
	public ExtraGuiElementArmorBars() {
		name = "armorBars";
		modes = new String[] {"default", "armorBars.smallBars", "armorBars.numbersOnly", "armorBars.barsNumbers", "armorBars.barsOnly", "armorBars.smallBarsOnly"};
		leftHeight = 74;
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		TextureManager tm = mc.getTextureManager();
		String currentMode = currentMode();
		
		int lh = BetterHud.currentLeftHeight;
		
		ItemStack[] armor = mc.thePlayer.inventory.armorInventory;
		boolean shouldRender = false;
		leftHeight = -5;
		
		for(ItemStack is : armor) {
			if(is != null) {
				shouldRender = true;
				leftHeight = 74;
				break;
			}
		}
		
		if(shouldRender) {
			for(int i = 0; i < 4; i++) {
				ItemStack currentArmor = armor[3 - i];
				if(currentArmor != null) {
					int maxDamage = currentArmor.getMaxDamage();
					int yOffset = 18 * i;
					float value = (float) (maxDamage - currentArmor.getItemDamage()) / (float) maxDamage;
					int green = (int) (255 * value);
					int red = 256 - green;
					
					mc.mcProfiler.startSection("items");
					RenderUtil.renderItem(ri, fr, tm, currentArmor, 5, lh + yOffset);
					mc.mcProfiler.endSection();
					
					mc.mcProfiler.startSection("bars");
					if(currentMode.equals("default")) {
						mc.ingameGUI.drawRect(26, lh + 11 + yOffset, 90, lh + 11 + yOffset + 2, 0xff000000);
						mc.ingameGUI.drawRect(26, lh + 11 + yOffset, (int) (26 + (value * 64)), lh + 11 + yOffset + 1, colorARGB(255, red, green, 0));
					} else if(currentMode.equals("armorBars.smallBars") || currentMode.equals("armorBars.smallBarsOnly")) {
						mc.ingameGUI.drawRect(22, lh + yOffset, 24, lh + yOffset + 16, 0xff000000);
						mc.ingameGUI.drawRect(22, lh + yOffset + 16 - (int) Math.ceil(value * 16), 23, lh + yOffset + 15, colorARGB(255, red, green, 0));
					} else if(currentMode.equals("armorBars.barsNumbers")) {
						mc.ingameGUI.drawRect(26, lh + 11 + yOffset, 58, lh + 11 + yOffset + 2, 0xff000000);
						mc.ingameGUI.drawRect(26, lh + 11 + yOffset, (int) (26 + (value * 32)), lh + 11 + yOffset + 1, colorARGB(255, red, green, 0));
					} else if(currentMode.equals("armorBars.barsOnly")) {
						mc.ingameGUI.drawRect(26, lh + 7 + yOffset, 58, lh + 7 + yOffset + 2, 0xff000000);
						mc.ingameGUI.drawRect(26, lh + 7 + yOffset, (int) (26 + (value * 32)), lh + 7 + yOffset + 1, colorARGB(255, red, green, 0));
					}
					mc.mcProfiler.endSection();
					
					if(!currentMode.equals("armorBars.barsOnly") && !currentMode.equals("armorBars.smallBarsOnly")) {
						mc.mcProfiler.startSection("text");
							String text = (currentMode.equals("default") || currentMode.equals("armorBars.smallBars") ? currentArmor.getDisplayName() + " - " : "") + (maxDamage - currentArmor.getItemDamage()) + "/" + maxDamage;
							mc.ingameGUI.drawString(fr, text, currentMode.equals("armorBars.smallBars") ? 28 : 26, lh + yOffset + (currentMode.equals("armorBars.smallBars") || currentMode.equals("armorBars.numbersOnly") ? 4 : 0), 0xffffff);
						mc.mcProfiler.endSection();
						
						mc.mcProfiler.startSection("textWarning");
						if(value <= 0.3F) {
							int amount = 3 - (int) (value * 10);
							String exclamation = "";
							for(int i2 = 0; i2 < amount; i2++) exclamation += "! ";
							mc.ingameGUI.drawString(fr, exclamation, fr.getStringWidth(text) + 32, lh + yOffset, 0xffff0000);
						}
						mc.mcProfiler.endSection();
					}
				}
			}
		}
	}
	
	protected static int colorARGB(int a, int r, int g, int b) {
		return (a << 24) + (r << 16) + (g << 8) + b;
	}
}
