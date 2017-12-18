package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import tk.nukeduck.hud.BetterHud;

import com.mojang.realmsclient.gui.ChatFormatting;

public class ExtraGuiElementSignReader extends ExtraGuiElement {
	public ResourceLocation signTex;
	public ExtraGuiElementSignReader() {
		name = "signReader";
		modes = new String[] {"sign.textLeft", "sign.visualLeft", "sign.textRight", "sign.visualRight"};
		defaultMode = 1;
		signTex = new ResourceLocation("textures/entity/sign.png");
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		MovingObjectPosition mop = mc.getRenderViewEntity().rayTrace(200, 1.0F);
		TileEntity te = mc.theWorld.getTileEntity(mop.getBlockPos());
		if(te != null && te instanceof TileEntitySign) {
			if(currentMode().startsWith("sign.text")) {
				ArrayList<String> sideStrings = currentMode().endsWith("Left") ? leftStrings : rightStrings;
				
				leftHeight = -5;
				rightHeight = -5;
				
				IChatComponent[] text = ((TileEntitySign) te).signText;
				sideStrings.add(ChatFormatting.RED + "-----SIGN-----");
				for(IChatComponent ic : text) {
					if(ic == null) ic = new ChatComponentText("");
					sideStrings.add(ic.getFormattedText());
				}
				sideStrings.add(ChatFormatting.RED + "--------------");
			} else {
				if(currentMode().endsWith("Left")) {
					leftHeight = fr.FONT_HEIGHT * 4 + 13;
					rightHeight = -5;
					
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					
					FMLClientHandler.instance().getClient().renderEngine.bindTexture(signTex);
					glPushMatrix();
					glTranslatef(5F, (float) BetterHud.currentLeftHeight, 0F);
					glScalef(1, 0.5F, 1); // Texture is treated as square rather than a rectangle
					mc.ingameGUI.drawTexturedModalRect(0, 0, 16, 16, 88, 96); // UVs multiplied by 8 for some reason
					glPopMatrix();
					
					int i = 0;
					for(IChatComponent ic : ((TileEntitySign) te).signText) {
						if(ic == null) ic = new ChatComponentText("");
						String s = ic.getFormattedText();
						fr.drawString(s, 49 - (fr.getStringWidth(s) / 2), BetterHud.currentLeftHeight + 2 + i * (fr.FONT_HEIGHT + 2), 0x000000);
						i++;
					}
				} else {
					rightHeight = fr.FONT_HEIGHT * 4 + 13;
					leftHeight = -5;
					
					FMLClientHandler.instance().getClient().renderEngine.bindTexture(signTex);
					glPushMatrix();
					glTranslatef(width - 93, (float) BetterHud.currentRightHeight, 0F);
					glScalef(1, 0.5F, 1); // Texture is treated as square rather than a rectangle
					mc.ingameGUI.drawTexturedModalRect(0, 0, 16, 16, 88, 96); // UVs multiplied by 8 for some reason
					glPopMatrix();
					
					int i = 0;
					if(te instanceof TileEntitySign) {
						for(IChatComponent ic : ((TileEntitySign) te).signText) {
							if(ic == null) ic = new ChatComponentText("");
							String s = ic.getFormattedText();
							fr.drawString(s, width - 49 - (fr.getStringWidth(s) / 2), BetterHud.currentRightHeight + 2 + i * (fr.FONT_HEIGHT + 2), 0x000000);
							i++;
						}
					}
				}
			}
			
		} else {
			leftHeight = -5;
		}
		
		// TODO Add better rendering for this, it doesn't look very good. Add an actually-rendered sign option
	}
}