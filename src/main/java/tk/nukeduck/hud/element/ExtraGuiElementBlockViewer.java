package tk.nukeduck.hud.element;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.util.FormatUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;

public class ExtraGuiElementBlockViewer extends ExtraGuiElement {
	public ExtraGuiElementBlockViewer() {
		name = "blockViewer";
		modes = new String[] {"topLeft", "topMiddle", "topRight", "bottomLeft", "bottomRight", "center"};
		defaultMode = 1;
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		MovingObjectPosition mop = mc.renderViewEntity.rayTrace(200, 1.0F);
		Block te = mc.theWorld.getBlock(mop.blockX, mop.blockY, mop.blockZ);
		if(te != null && !te.equals(Blocks.air)) {
			String ul = te.getUnlocalizedName() + ".name";
			String l = I18n.format(ul, new Object[] {});
			
			int x = halfWidth - 15 - fr.getStringWidth(l);
			int y = height / 2 - fr.FONT_HEIGHT - 15;
			
			String m = currentMode();
			if(m.equals("topLeft")) {
				x = BetterHud.getFromName(BetterHud.elements, "armorBars").enabled ? 185 : BetterHud.getFromName(BetterHud.elements, "signReader").enabled ? 105 : FormatUtil.getLongestWidth(fr, leftStrings) + 5;
				y = 5;
			} else if(m.equals("topMiddle")) {
				x = halfWidth - fr.getStringWidth(l) / 2 - 5;
				y = 50 + (BetterHud.getFromName(BetterHud.elements, "biome").enabled ? 10 : 0);
			} else if(m.equals("topRight")) {
				x = BetterHud.getFromName(BetterHud.elements, "clock").enabled ? width - 100 - fr.getStringWidth(l) : width - FormatUtil.getLongestWidth(fr, rightStrings) - fr.getStringWidth(l) - 25;
				y = 5;
			} else if(m.equals("bottomLeft")) {
				x = 5;
				y = height - 5 - fr.FONT_HEIGHT - 20;
			} else if(m.equals("bottomRight")) {
				x = width - 15 - fr.getStringWidth(l);
				y = height - fr.FONT_HEIGHT - 45;
			}
			
			renderBox(mc.ingameGUI, x, y, fr.getStringWidth(l) + 10, 10 + fr.FONT_HEIGHT, 0x3A000000);
			mc.ingameGUI.drawString(fr, l, x + 5, y + 5, 0xffffff);
			
			/*if(te.hasTileEntity(mc.theWorld.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ))) {
				TileEntity a = mc.theWorld.getTileEntity(mop.blockX, mop.blockY, mop.blockZ);
				if(a instanceof IInventory) {
					String guiName = ((IInventory) a).getInventoryName();
					mc.ingameGUI.drawString(fr, guiName, width - 5 - fr.getStringWidth(guiName), 86 + fr.FONT_HEIGHT, 0xffffff);
				}
			}*/
		}
	}
	
	public void renderBox(Gui gui, int x, int y, int width, int height, int color) {
		gui.drawRect(x + 1, y, x + width - 1, y + height, color);
		gui.drawRect(x, y + 1, x + 1, y + height - 1, color);
		gui.drawRect(x + width - 1, y + 1, x + width, y + height - 1, color);
		
		gui.drawRect(x + 1, y + 1, x + (width - 1), y + 2, color);
		gui.drawRect(x + 1, y + height - 2, x + (width - 1), y + height - 1, color);
		
		gui.drawRect(x + 1, y + 2, x + 2, y + height - 2, color);
		gui.drawRect(x + (width - 2), y + 2, x + (width - 1), y + height - 2, color);
	}
}