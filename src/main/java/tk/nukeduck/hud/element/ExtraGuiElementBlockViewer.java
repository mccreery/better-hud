package tk.nukeduck.hud.element;

import java.util.ArrayList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.RenderUtil;

public class ExtraGuiElementBlockViewer extends ExtraGuiElement {
	public ExtraGuiElementBlockViewer() {
		name = "blockViewer";
		modes = new String[] {"topLeft", "topMiddle", "topRight", "bottomLeft", "bottomRight", "center"};
		defaultMode = 1;
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		MovingObjectPosition mop = mc.getRenderViewEntity().rayTrace(200, 1.0F);
		IBlockState te = mc.theWorld.getBlockState(mop.getBlockPos());
		if(te != null && Item.getItemFromBlock(te.getBlock()) != null) {
			String l = new ItemStack(te.getBlock(), 1, te.getBlock().getMetaFromState(te)).getDisplayName();
			
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
			
			renderBox(x, y, fr.getStringWidth(l) + 10, 10 + fr.FONT_HEIGHT, 0x3A000000);
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
	
	public void renderBox(int x, int y, int width, int height, int color) {
		RenderUtil.drawRect(x + 1, y, x + width - 1, y + height, color);
		RenderUtil.drawRect(x, y + 1, x + 1, y + height - 1, color);
		RenderUtil.drawRect(x + width - 1, y + 1, x + width, y + height - 1, color);
		
		RenderUtil.drawRect(x + 1, y + 1, x + (width - 1), y + 2, color);
		RenderUtil.drawRect(x + 1, y + height - 2, x + (width - 1), y + height - 1, color);
		
		RenderUtil.drawRect(x + 1, y + 2, x + 2, y + height - 2, color);
		RenderUtil.drawRect(x + (width - 2), y + 2, x + (width - 1), y + height - 2, color);
	}
}