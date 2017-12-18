package tk.nukeduck.hud.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePosition;
import tk.nukeduck.hud.element.settings.ElementSettingMode;
import tk.nukeduck.hud.element.settings.ElementSettingPosition;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;

public class ExtraGuiElementFullInvIndicator extends ExtraGuiElement {
	private ElementSettingMode posMode;
	private ElementSettingPosition pos;
	private ElementSettingAbsolutePosition pos2;
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		posMode.index = 0;
		pos.value = Position.TOP_RIGHT;
		pos2.x = 5;
		pos2.y = 5;
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
	}
	
	public boolean isFull = false;
	
	public void update(Minecraft mc) {
		if(mc.thePlayer == null) {
			isFull = false;
			return;
		}
		for(int i = 0; i < mc.thePlayer.inventory.mainInventory.length; i++) {
			if(mc.thePlayer.inventory.mainInventory[i] == null) {
				isFull = false;
				return;
			}
		}
		for(int i = 0; i < mc.thePlayer.inventory.offHandInventory.length; i++) {
			if(mc.thePlayer.inventory.offHandInventory[i] == null) {
				isFull = false;
				return;
			}
		}
		isFull = true;
	}
	
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return this.posMode.index == 0 ? Bounds.EMPTY : new Bounds(pos2.x, pos2.y, Minecraft.getMinecraft().fontRendererObj.getStringWidth(FormatUtil.translatePre("strings.fullInv")), Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT);
	}
	
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		if(!isFull) return;
		
		if(posMode.index == 0) {
			stringManager.add(FormatUtil.translatePre("strings.fullInv"), pos.value);
		} else {
			mc.ingameGUI.drawString(mc.fontRendererObj, FormatUtil.translatePre("strings.fullInv"), pos2.x, pos2.y, RenderUtil.colorRGB(255, 255, 255));
		}
	}

	@Override
	public boolean shouldProfile() {
		return posMode.index == 1;
	}
}