package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.SettingAnchoredPosition;
import tk.nukeduck.hud.element.settings.SettingAnchor;
import tk.nukeduck.hud.element.settings.SettingMode;
import tk.nukeduck.hud.element.settings.SettingPosition.Position;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;

public class MaxLevelIndicator extends HudElement {
	private SettingMode posMode;
	private SettingAnchoredPosition pos;
	private SettingAnchor anchor;
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		posMode.index = 0;
		anchor.value = Position.TOP_LEFT.getFlag();
		pos.x = 5;
		pos.y = 5;
	}
	
	public MaxLevelIndicator() {
		super("enchantIndicator");
		this.settings.add(posMode = new SettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.anchor = new SettingAnchor("anchor");
		this.settings.add(pos = new SettingAnchoredPosition("position", anchor) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(anchor);
	}
	
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return new Bounds(pos.getEnabled() ? pos.x : resolution.getScaledWidth() / 2 - 8, pos.getEnabled() ? pos.y : resolution.getScaledHeight() - 50, 16, 16);
	}
	
	public void update() {}
	
	@Override
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {
		if(MC.player.capabilities.isCreativeMode
			|| MC.player.getRidingEntity() != null && MC.player.getRidingEntity() instanceof EntityHorse
			|| MC.player.experienceLevel < 30) return;
		
		this.pos.update(event.getResolution(), this.getBounds(event.getResolution()));
		int x = pos.getEnabled() ? pos.x : event.getResolution().getScaledWidth() / 2 - 8;
		int y = pos.getEnabled() ? pos.y : event.getResolution().getScaledHeight() - 50;
		
		RenderHelper.enableGUIStandardItemLighting();
		MC.getRenderItem().renderItemAndEffectIntoGUI(enchbook, x, y);
		RenderHelper.disableStandardItemLighting();
	}
	
	private static ItemStack enchbook = new ItemStack(Items.ENCHANTED_BOOK);

	@Override
	public boolean shouldProfile() {
		return true;
	}
}