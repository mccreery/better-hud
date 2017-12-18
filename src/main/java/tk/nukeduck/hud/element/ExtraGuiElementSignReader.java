package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.FMLClientHandler;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePositionAnchored;
import tk.nukeduck.hud.element.settings.ElementSettingAnchor;
import tk.nukeduck.hud.element.settings.ElementSettingDivider;
import tk.nukeduck.hud.element.settings.ElementSettingMode;
import tk.nukeduck.hud.element.settings.ElementSettingPosition;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.element.settings.ElementSettingPositionHorizontal;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Colors;

public class ExtraGuiElementSignReader extends ExtraGuiElement {
	private ElementSettingMode type;
	private ElementSettingMode posMode;
	private ElementSettingPosition pos;
	private ElementSettingAbsolutePositionAnchored pos2;
	private ElementSettingAnchor anchor;
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		type.index = 1;
		posMode.index = 0;
		pos.value = Position.MIDDLE_LEFT;
		anchor.value = Position.TOP_LEFT.getFlag();
		pos2.x = 5;
		pos2.y = 5;
	}
	
	@Override
	public String getName() {
		return "signReader";
	}
	
	public ResourceLocation signTex;
	
	public ExtraGuiElementSignReader() {
		signTex = new ResourceLocation("textures/entity/sign.png");
		this.settings.add(new ElementSettingDivider("position"));
		this.settings.add(posMode = new ElementSettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.settings.add(pos = new ElementSettingPositionHorizontal("position", Position.combine(Position.MIDDLE_LEFT, Position.MIDDLE_RIGHT)) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 0;
			}
		});
		this.anchor = new ElementSettingAnchor("anchor");
		this.settings.add(pos2 = new ElementSettingAbsolutePositionAnchored("position2", anchor) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(anchor);
		this.settings.add(new ElementSettingDivider("misc"));
		this.settings.add(type = new ElementSettingMode("mode", new String[] {"sign.text", "sign.visual"}));
	}
	
	public void update(Minecraft mc) {}
	
	private Bounds bounds = Bounds.EMPTY;
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return this.bounds;
	}
	
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		RayTraceResult mop = mc.getRenderViewEntity().rayTrace(200, 1.0F);
		TileEntity te = mc.world.getTileEntity(mop.getBlockPos());
		if(te != null && te instanceof TileEntitySign) {
			if(type.getValue() == "sign.text") {
				this.bounds = Bounds.EMPTY;
				//ArrayList<String> sideStrings = pos.location == Position.MIDDLE_LEFT ? leftStrings : rightStrings;
				Position strPos = pos.value == Position.MIDDLE_LEFT ? Position.TOP_LEFT : Position.TOP_RIGHT;
				
				if(posMode.index == 0) {
					ITextComponent[] text = ((TileEntitySign) te).signText;
					stringManager.add(ChatFormatting.RED + "-----SIGN-----", strPos);
					for(ITextComponent ic : text) {
						if(ic == null) ic = new TextComponentString("");
						stringManager.add(ic.getFormattedText(), strPos);
					}
					stringManager.add(ChatFormatting.RED + "--------------", strPos);
				} else {
					ITextComponent[] text = ((TileEntitySign) te).signText;
					ArrayList<String> items = new ArrayList<String>();
					items.add(ChatFormatting.RED + "-----SIGN-----");
					for(ITextComponent ic : text) {
						if(ic == null) ic = new TextComponentString("");
						items.add(ic.getFormattedText());
					}
					items.add(ChatFormatting.RED + "--------------");
					
					RenderUtil.renderStrings(mc.fontRenderer, items, pos2.x, pos2.y, Colors.WHITE, Position.TOP_LEFT);
				}
			} else {
				if(posMode.index == 1) {
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					
					FMLClientHandler.instance().getClient().renderEngine.bindTexture(signTex);
					glPushMatrix();
					glTranslatef(pos2.x, pos2.y, 0F);
					glScalef(1, 0.5F, 1); // Texture is treated as square rather than a rectangle
					mc.ingameGUI.drawTexturedModalRect(0, 0, 16, 16, 88, 96); // UVs multiplied by 8 for some reason
					glPopMatrix();
					
					pos2.update(resolution, new Bounds(0, 0, 88, 48));
					this.bounds = new Bounds(pos2.x, pos2.y, 88, 48);
					
					int i = 0;
					for(ITextComponent ic : ((TileEntitySign) te).signText) {
						if(ic == null) ic = new TextComponentString("");
						String s = ic.getFormattedText();
						mc.fontRenderer.drawString(s, pos2.x + 44 - mc.fontRenderer.getStringWidth(s) / 2, pos2.y + 2 + i * (mc.fontRenderer.FONT_HEIGHT + 2), Colors.BLACK);
						i++;
					}
				} else if(pos.value == Position.MIDDLE_LEFT) {
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					
					FMLClientHandler.instance().getClient().renderEngine.bindTexture(signTex);
					glPushMatrix();
					glTranslatef(5F, (float) layoutManager.get(Position.TOP_LEFT), 0F);
					glScalef(1, 0.5F, 1); // Texture is treated as square rather than a rectangle
					mc.ingameGUI.drawTexturedModalRect(0, 0, 16, 16, 88, 96); // UVs multiplied by 8 for some reason
					glPopMatrix();
					this.bounds = new Bounds(5, layoutManager.get(Position.TOP_LEFT), 88, 48);
					
					int i = 0;
					for(ITextComponent ic : ((TileEntitySign) te).signText) {
						if(ic == null) ic = new TextComponentString("");
						String s = ic.getFormattedText();
						mc.fontRenderer.drawString(s, 49 - (mc.fontRenderer.getStringWidth(s) / 2), layoutManager.get(Position.TOP_LEFT) + 2 + i * (mc.fontRenderer.FONT_HEIGHT + 2), Colors.BLACK);
						i++;
					}
					
					layoutManager.add(mc.fontRenderer.FONT_HEIGHT * 4 + 13, Position.TOP_LEFT);
				} else {
					FMLClientHandler.instance().getClient().renderEngine.bindTexture(signTex);
					glPushMatrix();
					glTranslatef(resolution.getScaledWidth() - 93, (float) layoutManager.get(Position.TOP_RIGHT), 0F);
					glScalef(1, 0.5F, 1); // Texture is treated as square rather than a rectangle
					mc.ingameGUI.drawTexturedModalRect(0, 0, 16, 16, 88, 96); // UVs multiplied by 8 for some reason
					glPopMatrix();
					
					this.bounds = new Bounds(resolution.getScaledWidth() - 93, layoutManager.get(Position.TOP_RIGHT), 88, 48);
					
					//int i = 0;
					int y = layoutManager.get(Position.TOP_RIGHT) + 2;
					if(te instanceof TileEntitySign) {
						for(ITextComponent ic : ((TileEntitySign) te).signText) {
							if(ic == null) ic = new TextComponentString("");
							String s = ic.getFormattedText();
							mc.fontRenderer.drawString(s, resolution.getScaledWidth() - 49 - (mc.fontRenderer.getStringWidth(s) / 2), y, Colors.BLACK);
							//i++;
							y += mc.fontRenderer.FONT_HEIGHT + 2;
						}
					}
					
					layoutManager.add(mc.fontRenderer.FONT_HEIGHT * 4 + 13, Position.TOP_RIGHT);
				}
			}
			
		}
	}
	
	@Override
	public boolean shouldProfile() {
		return true;
	}
}