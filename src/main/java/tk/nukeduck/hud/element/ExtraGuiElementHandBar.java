package tk.nukeduck.hud.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePosition;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePositionAnchored;
import tk.nukeduck.hud.element.settings.ElementSettingAnchor;
import tk.nukeduck.hud.element.settings.ElementSettingBoolean;
import tk.nukeduck.hud.element.settings.ElementSettingDivider;
import tk.nukeduck.hud.element.settings.ElementSettingMode;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;

public class ExtraGuiElementHandBar extends ExtraGuiElement {
	private ElementSettingMode posMode;
	private ElementSettingAbsolutePositionAnchored pos;
	private ElementSettingAnchor anchor;
	
	private ElementSettingBoolean showName;
	private ElementSettingBoolean showDurability;
	private ElementSettingMode durabilityMode;
	private ElementSettingBoolean showItem;
	private ElementSettingBoolean offHand;
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		posMode.index = 0;
		pos.x = 5;
		pos.y = 5;
		
		showName.value = true;
		showDurability.value = true;
		durabilityMode.index = 0;
		showItem.value = true;
		offHand.value = false;
	}
	
	@Override
	public String getName() {
		return "handBar";
	}
	
	public ExtraGuiElementHandBar() {
		//modes = new String[] {"default", "handBar.barOnly", "armorBars.numbersOnly"};
		//this.settings.add(mode = new ElementSettingMode("mode", new String[] {"default", "handBar.barOnly", "armorBars.numbersOnly"}));
		this.settings.add(new ElementSettingDivider("position"));
		this.settings.add(posMode = new ElementSettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.anchor = new ElementSettingAnchor("anchor");
		this.settings.add(pos = new ElementSettingAbsolutePositionAnchored("position", anchor) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(anchor);
		this.settings.add(new ElementSettingDivider("misc"));
		this.settings.add(showItem = new ElementSettingBoolean("showItem"));
		this.settings.add(showDurability = new ElementSettingBoolean("showDurability"));
		this.settings.add(durabilityMode = new ElementSettingMode("durabilityMode", new String[] {"values", "percent"}));
		this.settings.add(showName = new ElementSettingBoolean("showName"));
		this.settings.add(offHand = new ElementSettingBoolean("offHand"));
	}
	
	public void update(Minecraft mc) {}
	
	private Bounds bounds = Bounds.EMPTY;
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return this.bounds;
	}
	
	public void renderBar(Minecraft mc, ItemStack stack, int x, int y) {
		int maxDamage = stack.getMaxDamage();
		float value = (float) (maxDamage - stack.getItemDamage()) / (float) maxDamage;
		byte green = (byte) (255 * value);
		byte red = (byte) (256 - green);
		
		String text = "";
		
		String dur;
		if(durabilityMode.getValue().equals("percent")) {
			dur = FormatUtil.translatePre("strings.percent", ExtraGuiElementArmorBars.ONE_PLACE.format(value * 100.0));
		} else {
			dur = FormatUtil.translatePre("strings.outOf", String.valueOf(maxDamage - stack.getItemDamage()), String.valueOf(maxDamage));
		}
		
		if(showName.value && showDurability.value) {
			text = FormatUtil.translatePre("strings.separated", stack.getDisplayName(), dur);
		} else if(showName.value) {
			text = stack.getDisplayName();
		} else if(showDurability.value) {
			text = dur;
		}
		
		int totalWidth = mc.fontRendererObj.getStringWidth(text);
		if(showItem.value) totalWidth += 21;
		
		if(showItem.value) {
			mc.mcProfiler.startSection("items");
			mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			RenderUtil.renderItem(mc.getRenderItem(), mc.fontRendererObj, mc.getTextureManager(), stack, x + 90 - totalWidth / 2, y);
			mc.mcProfiler.endSection();
		}
		
		mc.mcProfiler.startSection("text");
		mc.ingameGUI.drawString(mc.fontRendererObj, text, x + 90 - totalWidth / 2 + (showItem.value ? 21 : 0), y + 4, RenderUtil.colorRGB(255, 255, 255));
		mc.mcProfiler.endSection();
		
		mc.mcProfiler.startSection("bars");
		RenderUtil.drawRect(x, y + 16, x + 180, y + 18, RenderUtil.colorARGB(255, 0, 0, 0));
		RenderUtil.drawRect(x, y + 16, x + Math.round((value * 180)), y + 17, RenderUtil.colorARGB(255, red, green, 0));
		mc.mcProfiler.endSection();
	}
	
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		int x = posMode.index == 0 ? resolution.getScaledWidth() / 2 - 90 : pos.x;
		int y = posMode.index == 0 ? resolution.getScaledHeight() - (offHand.value ? 107 : 84) : pos.y;
		this.bounds = new Bounds(x, y, 180, offHand.value ? 41 : 18);
		// updates after, but that's alright - only one frame when you enable or disable offhand
		this.pos.update(resolution, this.bounds);

		ItemStack stack = mc.thePlayer.getHeldItemMainhand();
		if(stack != null && stack.getMaxDamage() > 0) {
			renderBar(mc, stack, x, this.bounds.getY2() - 18);
		}
		if(offHand.value) {
			stack = mc.thePlayer.getHeldItemOffhand();
			if(stack != null && stack.getMaxDamage() > 0) {
				renderBar(mc, stack, x, this.bounds.getY());
			}
		}
	}

	@Override
	public boolean shouldProfile() {
		return true;
	}
}