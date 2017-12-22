package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.SettingAnchoredPosition;
import tk.nukeduck.hud.element.settings.SettingAnchor;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.Divider;
import tk.nukeduck.hud.element.settings.SettingMode;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingPosition.Position;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Colors;

public class Clock extends HudElement {
	private SettingMode posMode;
	private SettingPosition pos;
	private SettingAnchoredPosition pos2;
	private SettingAnchor anchor;
	private SettingBoolean twentyFour;
	
	private Bounds bounds = Bounds.EMPTY.clone();
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return bounds;
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
	
	public Clock() {
		super("clock");
		staticHeight = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * 2 + 12;
		
		this.settings.add(new Divider("position"));
		this.settings.add(posMode = new SettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.settings.add(pos = new SettingPosition("position", Position.CORNERS) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 0;
			}
		});
		this.anchor = new SettingAnchor("anchor");
		this.settings.add(pos2 = new SettingAnchoredPosition("position2", anchor) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(anchor);
		this.settings.add(new Divider("misc"));
		this.settings.add(twentyFour = new SettingBoolean("24hr"));
	}
	
	int staticHeight;
	
	//public static final long night = 18541;
	//public static final long morning = 5458;
	
	private static final ItemStack bed = new ItemStack(Items.BED, 1);
	
	public void update() {}
	
	@Override
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {
		boolean twentyFourHour = twentyFour.value;
		
		boolean canSleep = !MC.world.isDaytime();
		
		// TODO replace hundreds of getResolution().getScaled with vars
		
		long t = (MC.world.getWorldTime() + 6000) % 24000;
		String day = I18n.format("betterHud.strings.day", (MC.world.getWorldTime() + 6000) / 24000 + 1);
		String time;
		int h = (int) (t / 1000);
		
		time = FormatUtil.formatTime(h, (int) ((t % 1000) / 1000.0 * 60.0), twentyFourHour);

		int width = event.getResolution().getScaledWidth();
		int height = event.getResolution().getScaledHeight();
		
		int staticWidth = Math.max(MC.fontRenderer.getStringWidth(time), MC.fontRenderer.getStringWidth(day)) + 10;
		
		if(posMode.index == 1) {
			int x = pos2.x;
			int y = pos2.y;
			
			RenderUtil.renderQuad(Tessellator.getInstance(), x, y, staticWidth, staticHeight, Colors.TRANSLUCENT);
			//RenderUtil.drawRect(x, y, x + staticWidth, y + staticHeight, Colors.TRANSLUCENT);
			MC.ingameGUI.drawString(MC.fontRenderer, time, x + 5, y + 5, Colors.WHITE);
			MC.ingameGUI.drawString(MC.fontRenderer, day, x + 5, y + 7 + MC.fontRenderer.FONT_HEIGHT, Colors.WHITE);
			
			if(canSleep) { // Night: 6:32 PM - 5:28 AM... Of course
				MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				RenderUtil.renderItem(MC.getRenderItem(), MC.fontRenderer, MC.getTextureManager(), bed, x + Math.max(MC.fontRenderer.getStringWidth(time), MC.fontRenderer.getStringWidth(day)) + 15, y + 8);
			}
			
			bounds.setX(x);
			bounds.setY(y);
			bounds.setWidth(staticWidth);
			bounds.setHeight(staticHeight);
			this.pos2.update(event.getResolution(), this.bounds);
		} else {
			int currentHeight = layoutManager.get(pos.value);
			// TODO cool repetitive code dude
			switch(pos.value) {
				case TOP_LEFT:
					this.bounds = new Bounds(0, currentHeight, staticWidth, staticHeight);
					RenderUtil.renderQuad(Tessellator.getInstance(), 0, currentHeight, staticWidth, staticHeight, Colors.TRANSLUCENT);
					//RenderUtil.drawRect(0, currentHeight, staticWidth, currentHeight + staticHeight, Colors.TRANSLUCENT);
					MC.ingameGUI.drawString(MC.fontRenderer, time, 5, currentHeight + 5, Colors.WHITE);
					MC.ingameGUI.drawString(MC.fontRenderer, day, 5, currentHeight + 7 + MC.fontRenderer.FONT_HEIGHT, Colors.WHITE);
					
					if(canSleep) { // Night: 6:32 PM - 5:28 AM... Of course
						MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
						RenderUtil.renderItem(MC.getRenderItem(), MC.fontRenderer, MC.getTextureManager(), bed, Math.max(MC.fontRenderer.getStringWidth(time), MC.fontRenderer.getStringWidth(day)) + 15, currentHeight + 8);
					}
					break;
				case TOP_RIGHT:
					this.bounds = new Bounds(width - staticWidth, currentHeight, staticWidth, staticHeight);
					RenderUtil.renderQuad(Tessellator.getInstance(), width - staticWidth, currentHeight, staticWidth, staticHeight, Colors.TRANSLUCENT);
					//RenderUtil.drawRect(resolution.getScaledWidth() - staticWidth, currentHeight, resolution.getScaledWidth(), currentHeight + staticHeight, Colors.TRANSLUCENT);
					MC.ingameGUI.drawString(MC.fontRenderer, time, width - MC.fontRenderer.getStringWidth(time) - 5, currentHeight + 5, Colors.WHITE);
					MC.ingameGUI.drawString(MC.fontRenderer, day, width - MC.fontRenderer.getStringWidth(day) - 5, currentHeight + 7 + MC.fontRenderer.FONT_HEIGHT, Colors.WHITE);
					
					if(canSleep) { // Night: 6:32 PM - 5:28 AM... Of course
						MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
						RenderUtil.renderItem(MC.getRenderItem(), MC.fontRenderer, MC.getTextureManager(), bed, width - Math.max(MC.fontRenderer.getStringWidth(time), MC.fontRenderer.getStringWidth(day)) - 31, currentHeight + 8);
					}
					break;
				case BOTTOM_LEFT:
					this.bounds = new Bounds(0, height - currentHeight - staticHeight, staticWidth, staticHeight);
					RenderUtil.renderQuad(Tessellator.getInstance(), 0, height - currentHeight - staticHeight, staticWidth, staticHeight, Colors.TRANSLUCENT);
					//RenderUtil.drawRect(0, resolution.getScaledHeight() - currentHeight - staticHeight, staticWidth, resolution.getScaledHeight() - currentHeight, Colors.TRANSLUCENT);
					MC.ingameGUI.drawString(MC.fontRenderer, time, 5, height - currentHeight - staticHeight + 5, Colors.WHITE);
					MC.ingameGUI.drawString(MC.fontRenderer, day, 5, height - currentHeight - staticHeight + 7 + MC.fontRenderer.FONT_HEIGHT, Colors.WHITE);
					
					if(canSleep) { // Night: 6:32 PM - 5:28 AM... Of course
						MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
						RenderUtil.renderItem(MC.getRenderItem(), MC.fontRenderer, MC.getTextureManager(), bed, Math.max(MC.fontRenderer.getStringWidth(time), MC.fontRenderer.getStringWidth(day)) + 15, height - currentHeight - staticHeight + 8);
					}
					break;
				case BOTTOM_RIGHT:
					this.bounds = new Bounds(width - staticWidth, height - currentHeight - staticHeight, staticWidth, staticHeight);
					RenderUtil.renderQuad(Tessellator.getInstance(), width - staticWidth, height - currentHeight - staticHeight, staticWidth, staticHeight, Colors.TRANSLUCENT);
					//RenderUtil.drawRect(resolution.getScaledWidth() - staticWidth, resolution.getScaledHeight() - currentHeight - staticHeight, resolution.getScaledWidth(), resolution.getScaledHeight() - currentHeight, Colors.TRANSLUCENT);
					MC.ingameGUI.drawString(MC.fontRenderer, time, width - MC.fontRenderer.getStringWidth(time) - 5, height - currentHeight - staticHeight + 5, Colors.WHITE);
					MC.ingameGUI.drawString(MC.fontRenderer, day, width - MC.fontRenderer.getStringWidth(day) - 5, height - currentHeight - staticHeight + 7 + MC.fontRenderer.FONT_HEIGHT, Colors.WHITE);
					
					if(canSleep) { // Night: 6:32 PM - 5:28 AM... Of course
						MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
						RenderUtil.renderItem(MC.getRenderItem(), MC.fontRenderer, MC.getTextureManager(), bed, width - Math.max(MC.fontRenderer.getStringWidth(time), MC.fontRenderer.getStringWidth(day)) - 31, height - currentHeight - staticHeight + 8);
					}
					break;
				default:
			}
			layoutManager.add(staticHeight, pos.value);
		}
		
		/*if(right) {
			int rightHeight = layoutManager.get(Position.TOP_RIGHT);
			
			RenderUtil.drawRect(width - staticWidth, rightHeight, width, rightHeight + staticHeight, RenderUtil.colorARGB(85, 0, 0, 0));
			MC.ingameGUI.drawString(fr, time, width - fr.getStringWidth(time) - 5, rightHeight + 5, Colors.WHITE);
			MC.ingameGUI.drawString(fr, day, width - fr.getStringWidth(day) - 5, rightHeight + 7 + fr.FONT_HEIGHT, Colors.WHITE);
			
			if(t >= night || t <= morning) { // Night: 6:32 PM - 5:28 AM... Of course
				BetterHud.MC.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
				RenderUtil.renderItem(ri, fr, MC.getTextureManager(), bed, width - Math.max(fr.getStringWidth(time), fr.getStringWidth(day)) - 31, rightHeight + 8);
				//MC.getTextureManager().bindTexture(ExtraGuiElementBlood.blood);
				//BetterHud.itemRendererGui.drawTexturedModalRect(width - Math.max(fr.getStringWidth(time), fr.getStringWidth(day)) - 31, BetterHud.currentRightHeight + 8, 64, 64, 16, 16);
			}
			
			layoutManager.add(staticHeight, Position.TOP_RIGHT);
		} else {
			int leftHeight = layoutManager.get(Position.TOP_LEFT);
			
			RenderUtil.drawRect(0, leftHeight, staticWidth, leftHeight + staticHeight, RenderUtil.colorARGB(85, 0, 0, 0));
			MC.ingameGUI.drawString(fr, time, 5, leftHeight + 5, Colors.WHITE);
			MC.ingameGUI.drawString(fr, day, 5, leftHeight + 7 + fr.FONT_HEIGHT, Colors.WHITE);
			
			if(t >= night || t <= morning) { // Night: 6:32 PM - 5:28 AM... Of course
				BetterHud.MC.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
				RenderUtil.renderItem(ri, fr, MC.getTextureManager(), bed, width - Math.max(fr.getStringWidth(time), fr.getStringWidth(day)) + 15, leftHeight + 8);
				//MC.getTextureManager().bindTexture(ExtraGuiElementBlood.blood);
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