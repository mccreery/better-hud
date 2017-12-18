package tk.nukeduck.hud.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePosition;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePositionAnchored;
import tk.nukeduck.hud.element.settings.ElementSettingAnchor;
import tk.nukeduck.hud.element.settings.ElementSettingBoolean;
import tk.nukeduck.hud.element.settings.ElementSettingDivider;
import tk.nukeduck.hud.element.settings.ElementSettingMode;
import tk.nukeduck.hud.element.settings.ElementSettingPosition;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;

public class ExtraGuiElementClock extends ExtraGuiElement {
	private ElementSettingMode posMode;
	private ElementSettingPosition pos;
	private ElementSettingAbsolutePositionAnchored pos2;
	private ElementSettingAnchor anchor;
	private ElementSettingBoolean twentyFour;
	
	private Bounds bounds = Bounds.EMPTY.clone();
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return this.bounds;
	}
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		posMode.index = 0;
		pos.value = Position.TOP_RIGHT;
		anchor.value = Position.TOP_LEFT.getFlag();
		pos2.x = 0;
		pos2.y = 5;
		twentyFour.value = false;
	}
	
	@Override
	public String getName() {
		return "clock";
	}
	
	public ExtraGuiElementClock() {
		staticHeight = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * 2 + 12;
		
		this.settings.add(new ElementSettingDivider("position"));
		this.settings.add(posMode = new ElementSettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.settings.add(pos = new ElementSettingPosition("position", Position.CORNERS) {
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
		this.settings.add(twentyFour = new ElementSettingBoolean("24hr"));
	}
	
	int staticHeight;
	
	//public static final long night = 18541;
	//public static final long morning = 5458;
	
	private static final ItemStack bed = new ItemStack(Items.BED, 1);
	
	public void update(Minecraft mc) {}
	
	@Override
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		boolean twentyFourHour = twentyFour.value;
		
		boolean canSleep = !mc.theWorld.isDaytime();
		
		long t = (mc.theWorld.getWorldTime() + 6000) % 24000;
		String day = "Day " + ((mc.theWorld.getWorldTime() + 6000) / 24000 + 1);
		String time;
		int h = (int) (t / 1000);
		
		time = FormatUtil.formatTime(h, (int) ((t % 1000) / 1000.0 * 60.0), twentyFourHour);
		
		int staticWidth = Math.max(mc.fontRendererObj.getStringWidth(time), mc.fontRendererObj.getStringWidth(day)) + 10;
		
		if(posMode.index == 1) {
			int x = pos2.x;
			int y = pos2.y;
			
			RenderUtil.drawRect(x, y, x + staticWidth, y + staticHeight, RenderUtil.colorARGB(85, 0, 0, 0));
			mc.ingameGUI.drawString(mc.fontRendererObj, time, x + 5, y + 5, RenderUtil.colorRGB(255, 255, 255));
			mc.ingameGUI.drawString(mc.fontRendererObj, day, x + 5, y + 7 + mc.fontRendererObj.FONT_HEIGHT, RenderUtil.colorRGB(255, 255, 255));
			
			if(canSleep) { // Night: 6:32 PM - 5:28 AM... Of course
				mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				RenderUtil.renderItem(mc.getRenderItem(), mc.fontRendererObj, mc.getTextureManager(), bed, x + Math.max(mc.fontRendererObj.getStringWidth(time), mc.fontRendererObj.getStringWidth(day)) + 15, y + 8);
			}
			
			bounds.setX(x);
			bounds.setY(y);
			bounds.setWidth(staticWidth);
			bounds.setHeight(staticHeight);
			this.pos2.update(resolution, this.bounds);
		} else {
			int currentHeight = layoutManager.get(pos.value);
			switch(pos.value) {
				case TOP_LEFT:
					this.bounds = new Bounds(0, currentHeight, staticWidth, staticHeight);
					RenderUtil.drawRect(0, currentHeight, staticWidth, currentHeight + staticHeight, RenderUtil.colorARGB(85, 0, 0, 0));
					mc.ingameGUI.drawString(mc.fontRendererObj, time, 5, currentHeight + 5, RenderUtil.colorRGB(255, 255, 255));
					mc.ingameGUI.drawString(mc.fontRendererObj, day, 5, currentHeight + 7 + mc.fontRendererObj.FONT_HEIGHT, RenderUtil.colorRGB(255, 255, 255));
					
					if(canSleep) { // Night: 6:32 PM - 5:28 AM... Of course
						mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
						RenderUtil.renderItem(mc.getRenderItem(), mc.fontRendererObj, mc.getTextureManager(), bed, Math.max(mc.fontRendererObj.getStringWidth(time), mc.fontRendererObj.getStringWidth(day)) + 15, currentHeight + 8);
					}
					break;
				case TOP_RIGHT:
					this.bounds = new Bounds(resolution.getScaledWidth() - staticWidth, currentHeight, staticWidth, staticHeight);
					RenderUtil.drawRect(resolution.getScaledWidth() - staticWidth, currentHeight, resolution.getScaledWidth(), currentHeight + staticHeight, RenderUtil.colorARGB(85, 0, 0, 0));
					mc.ingameGUI.drawString(mc.fontRendererObj, time, resolution.getScaledWidth() - mc.fontRendererObj.getStringWidth(time) - 5, currentHeight + 5, RenderUtil.colorRGB(255, 255, 255));
					mc.ingameGUI.drawString(mc.fontRendererObj, day, resolution.getScaledWidth() - mc.fontRendererObj.getStringWidth(day) - 5, currentHeight + 7 + mc.fontRendererObj.FONT_HEIGHT, RenderUtil.colorRGB(255, 255, 255));
					
					if(canSleep) { // Night: 6:32 PM - 5:28 AM... Of course
						mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
						RenderUtil.renderItem(mc.getRenderItem(), mc.fontRendererObj, mc.getTextureManager(), bed, resolution.getScaledWidth() - Math.max(mc.fontRendererObj.getStringWidth(time), mc.fontRendererObj.getStringWidth(day)) - 31, currentHeight + 8);
					}
					break;
				case BOTTOM_LEFT:
					this.bounds = new Bounds(0, resolution.getScaledHeight() - currentHeight - staticHeight, staticWidth, staticHeight);
					RenderUtil.drawRect(0, resolution.getScaledHeight() - currentHeight - staticHeight, staticWidth, resolution.getScaledHeight() - currentHeight, RenderUtil.colorARGB(85, 0, 0, 0));
					mc.ingameGUI.drawString(mc.fontRendererObj, time, 5, resolution.getScaledHeight() - currentHeight - staticHeight + 5, RenderUtil.colorRGB(255, 255, 255));
					mc.ingameGUI.drawString(mc.fontRendererObj, day, 5, resolution.getScaledHeight() - currentHeight - staticHeight + 7 + mc.fontRendererObj.FONT_HEIGHT, RenderUtil.colorRGB(255, 255, 255));
					
					if(canSleep) { // Night: 6:32 PM - 5:28 AM... Of course
						mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
						RenderUtil.renderItem(mc.getRenderItem(), mc.fontRendererObj, mc.getTextureManager(), bed, Math.max(mc.fontRendererObj.getStringWidth(time), mc.fontRendererObj.getStringWidth(day)) + 15, resolution.getScaledHeight() - currentHeight - staticHeight + 8);
					}
					break;
				case BOTTOM_RIGHT:
					this.bounds = new Bounds(resolution.getScaledWidth() - staticWidth, resolution.getScaledHeight() - currentHeight - staticHeight, staticWidth, staticHeight);
					RenderUtil.drawRect(resolution.getScaledWidth() - staticWidth, resolution.getScaledHeight() - currentHeight - staticHeight, resolution.getScaledWidth(), resolution.getScaledHeight() - currentHeight, RenderUtil.colorARGB(85, 0, 0, 0));
					mc.ingameGUI.drawString(mc.fontRendererObj, time, resolution.getScaledWidth() - mc.fontRendererObj.getStringWidth(time) - 5, resolution.getScaledHeight() - currentHeight - staticHeight + 5, RenderUtil.colorRGB(255, 255, 255));
					mc.ingameGUI.drawString(mc.fontRendererObj, day, resolution.getScaledWidth() - mc.fontRendererObj.getStringWidth(day) - 5, resolution.getScaledHeight() - currentHeight - staticHeight + 7 + mc.fontRendererObj.FONT_HEIGHT, RenderUtil.colorRGB(255, 255, 255));
					
					if(canSleep) { // Night: 6:32 PM - 5:28 AM... Of course
						mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
						RenderUtil.renderItem(mc.getRenderItem(), mc.fontRendererObj, mc.getTextureManager(), bed, resolution.getScaledWidth() - Math.max(mc.fontRendererObj.getStringWidth(time), mc.fontRendererObj.getStringWidth(day)) - 31, resolution.getScaledHeight() - currentHeight - staticHeight + 8);
					}
					break;
				default:
			}
			layoutManager.add(staticHeight, pos.value);
		}
		
		/*if(right) {
			int rightHeight = layoutManager.get(Position.TOP_RIGHT);
			
			RenderUtil.drawRect(width - staticWidth, rightHeight, width, rightHeight + staticHeight, RenderUtil.colorARGB(85, 0, 0, 0));
			mc.ingameGUI.drawString(fr, time, width - fr.getStringWidth(time) - 5, rightHeight + 5, RenderUtil.colorRGB(255, 255, 255));
			mc.ingameGUI.drawString(fr, day, width - fr.getStringWidth(day) - 5, rightHeight + 7 + fr.FONT_HEIGHT, RenderUtil.colorRGB(255, 255, 255));
			
			if(t >= night || t <= morning) { // Night: 6:32 PM - 5:28 AM... Of course
				BetterHud.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
				RenderUtil.renderItem(ri, fr, mc.getTextureManager(), bed, width - Math.max(fr.getStringWidth(time), fr.getStringWidth(day)) - 31, rightHeight + 8);
				//mc.getTextureManager().bindTexture(ExtraGuiElementBlood.blood);
				//BetterHud.itemRendererGui.drawTexturedModalRect(width - Math.max(fr.getStringWidth(time), fr.getStringWidth(day)) - 31, BetterHud.currentRightHeight + 8, 64, 64, 16, 16);
			}
			
			layoutManager.add(staticHeight, Position.TOP_RIGHT);
		} else {
			int leftHeight = layoutManager.get(Position.TOP_LEFT);
			
			RenderUtil.drawRect(0, leftHeight, staticWidth, leftHeight + staticHeight, RenderUtil.colorARGB(85, 0, 0, 0));
			mc.ingameGUI.drawString(fr, time, 5, leftHeight + 5, RenderUtil.colorRGB(255, 255, 255));
			mc.ingameGUI.drawString(fr, day, 5, leftHeight + 7 + fr.FONT_HEIGHT, RenderUtil.colorRGB(255, 255, 255));
			
			if(t >= night || t <= morning) { // Night: 6:32 PM - 5:28 AM... Of course
				BetterHud.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
				RenderUtil.renderItem(ri, fr, mc.getTextureManager(), bed, width - Math.max(fr.getStringWidth(time), fr.getStringWidth(day)) + 15, leftHeight + 8);
				//mc.getTextureManager().bindTexture(ExtraGuiElementBlood.blood);
				//BetterHud.itemRendererGui.drawTexturedModalRect(Math.max(fr.getStringWidth(time), fr.getStringWidth(day)) + 15, BetterHud.currentLeftHeight + 8, 64, 64, 16, 16);
			}
			
			layoutManager.add(staticHeight, Position.TOP_LEFT);
		}*/
	}

	@Override
	public boolean shouldProfile() {
		return true;
	}
}