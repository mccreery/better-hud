package tk.nukeduck.hud.element.entityinfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;
import tk.nukeduck.hud.element.ExtraGuiElement;
import tk.nukeduck.hud.element.settings.ElementSettingSlider;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;

public abstract class ExtraGuiElementEntityInfo extends ExtraGuiElement {
	public ElementSettingSlider distance;
	
	public void update(Minecraft mc) {}
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {}
	public abstract void renderInfo(EntityLivingBase entity, Minecraft mc, float partialTicks);
	
	@Override
	public boolean shouldRender() {
		return false;
	}
	
	@Override
	public boolean shouldProfile() {
		return false;
	}
}