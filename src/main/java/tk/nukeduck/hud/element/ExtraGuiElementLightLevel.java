package tk.nukeduck.hud.element;

import java.util.ArrayList;

import tk.nukeduck.hud.util.FormatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;

public class ExtraGuiElementLightLevel extends ExtraGuiElement {
	public ExtraGuiElementLightLevel() {
		name = "lightLevel";
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
	    int j3 = MathHelper.floor_double(mc.thePlayer.posX);
	    int k3 = MathHelper.floor_double(mc.thePlayer.posY);
	    int l3 = MathHelper.floor_double(mc.thePlayer.posZ);
		
	    BlockPos blockpos = new BlockPos(j3, k3, l3);
	    
	    if(mc.theWorld != null && mc.theWorld.isBlockLoaded(blockpos)) {
	    	int light = mc.theWorld.getLightFor(EnumSkyBlock.SKY, blockpos) - mc.theWorld.calculateSkylightSubtracted(1.0F);
	    	light = Math.max(light, mc.theWorld.getLightFor(EnumSkyBlock.BLOCK, blockpos));
	    	
			String lightLevelString = FormatUtil.translatePre("strings.lightLevel", String.valueOf(light > 15 ? 15 : light));
			mc.ingameGUI.drawString(fr, lightLevelString, width - 5 - fr.getStringWidth(lightLevelString), height - 5 - fr.FONT_HEIGHT, 0xffffffff);
	    }
	}
}