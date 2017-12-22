package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.SettingAbsolutePosition;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingMode;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingPosition.Position;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.Ticker;
import tk.nukeduck.hud.util.constants.Colors;

public class FullInvIndicator extends HudElement {
	private SettingMode posMode;
	private SettingPosition pos;
	private SettingAbsolutePosition pos2;
	private SettingBoolean offHand;
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		posMode.index = 0;
		pos.value = Position.TOP_RIGHT;
		pos2.x = 5;
		pos2.y = 5;
		offHand.value = false;
	}
	
	public FullInvIndicator() {
		super("fullInvIndicator");
		Ticker.FASTER.register(this);
		this.settings.add(posMode = new SettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.settings.add(pos = new SettingPosition("position", Position.CORNERS) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 0;
			}
		});
		this.settings.add(pos2 = new SettingAbsolutePosition("position2") {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(offHand = new SettingBoolean("offhand"));
	}
	
	public boolean isFull = false;

	public void update() {
		if(MC.player == null) return;

		this.isFull = MC.player.inventory.getFirstEmptyStack() == -1 &&
			(!offHand.value || !MC.player.inventory.offHandInventory.get(0).isEmpty());
	}
	
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return this.posMode.index == 0 ? Bounds.EMPTY : new Bounds(
			pos2.x, pos2.y,
			Minecraft.getMinecraft().fontRenderer.getStringWidth(I18n.format("betterHud.strings.fullInv")),
			Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);
	}
	
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {
		if(!this.isFull) return;

		if(posMode.index == 0) {
			stringManager.add(I18n.format("betterHud.strings.fullInv"), pos.value);
		} else {
			MC.ingameGUI.drawString(MC.fontRenderer, I18n.format("betterHud.strings.fullInv"), pos2.x, pos2.y, Colors.WHITE);
		}
	}

	@Override
	public boolean shouldProfile() {
		return posMode.index == 1;
	}
}