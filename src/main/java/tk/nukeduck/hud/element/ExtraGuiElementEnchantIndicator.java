package tk.nukeduck.hud.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePosition;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePositionAnchored;
import tk.nukeduck.hud.element.settings.ElementSettingAnchor;
import tk.nukeduck.hud.element.settings.ElementSettingMode;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;

public class ExtraGuiElementEnchantIndicator extends ExtraGuiElement {
	private ElementSettingMode posMode;
	private ElementSettingAbsolutePositionAnchored pos;
	private ElementSettingAnchor anchor;
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		posMode.index = 0;
		anchor.value = Position.TOP_LEFT.getFlag();
		pos.x = 5;
		pos.y = 5;
	}
	
	public ExtraGuiElementEnchantIndicator() {
		this.settings.add(posMode = new ElementSettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.anchor = new ElementSettingAnchor("anchor");
		this.settings.add(pos = new ElementSettingAbsolutePositionAnchored("position", anchor) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(anchor);
	}
	
	@Override
	public String getName() {
		return "enchantIndicator";
	}
	
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return new Bounds(pos.getEnabled() ? pos.x : resolution.getScaledWidth() / 2 - 8, pos.getEnabled() ? pos.y : resolution.getScaledHeight() - 50, 16, 16);
	}
	
	public void update(Minecraft mc) {}
	
	@Override
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		if(mc.thePlayer.capabilities.isCreativeMode
			|| mc.thePlayer.getRidingEntity() != null && mc.thePlayer.getRidingEntity() instanceof EntityHorse
			|| mc.thePlayer.experienceLevel < 30) return;
		
		this.pos.update(resolution, this.getBounds(resolution));
		int x = pos.getEnabled() ? pos.x : resolution.getScaledWidth() / 2 - 8;
		int y = pos.getEnabled() ? pos.y : resolution.getScaledHeight() - 50;
		
		RenderHelper.enableGUIStandardItemLighting();
		mc.getRenderItem().renderItemAndEffectIntoGUI(enchbook, x, y);
		RenderHelper.disableStandardItemLighting();
	}
	
	private static ItemStack enchbook = new ItemStack(Items.ENCHANTED_BOOK);

	@Override
	public boolean shouldProfile() {
		return true;
	}
}