package tk.nukeduck.hud.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePosition;
import tk.nukeduck.hud.element.settings.ElementSettingBoolean;
import tk.nukeduck.hud.element.settings.ElementSettingMode;
import tk.nukeduck.hud.element.settings.ElementSettingPosition;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Colors;

public class ExtraGuiElementFullInvIndicator extends ExtraGuiElement {
	private ElementSettingMode posMode;
	private ElementSettingPosition pos;
	private ElementSettingAbsolutePosition pos2;
	private ElementSettingBoolean offHand;
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		posMode.index = 0;
		pos.value = Position.TOP_RIGHT;
		pos2.x = 5;
		pos2.y = 5;
		offHand.value = false;
	}
	
	@Override
	public String getName() {
		return "fullInvIndicator";
	}
	
	public ExtraGuiElementFullInvIndicator() {
		//modes = new String[] {"left", "right"};
		this.registerUpdates(UpdateSpeed.FASTER);
		this.settings.add(posMode = new ElementSettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.settings.add(pos = new ElementSettingPosition("position", Position.CORNERS) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 0;
			}
		});
		this.settings.add(pos2 = new ElementSettingAbsolutePosition("position2") {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(offHand = new ElementSettingBoolean("offhand"));
	}
	
	public boolean isFull = false;

	public void update(Minecraft mc) {
		if(mc.player == null) return;

		this.isFull = mc.player.inventory.getFirstEmptyStack() == -1 &&
			(!offHand.value || !mc.player.inventory.offHandInventory.get(0).isEmpty());
	}
	
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return this.posMode.index == 0 ? Bounds.EMPTY : new Bounds(
			pos2.x, pos2.y,
			Minecraft.getMinecraft().fontRenderer.getStringWidth(I18n.format("betterHud.strings.fullInv")),
			Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);
	}
	
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		if(!this.isFull) return;

		if(posMode.index == 0) {
			stringManager.add(I18n.format("betterHud.strings.fullInv"), pos.value);
		} else {
			mc.ingameGUI.drawString(mc.fontRenderer, I18n.format("betterHud.strings.fullInv"), pos2.x, pos2.y, Colors.WHITE);
		}
	}

	@Override
	public boolean shouldProfile() {
		return posMode.index == 1;
	}
}