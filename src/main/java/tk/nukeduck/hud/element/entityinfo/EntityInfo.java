package tk.nukeduck.hud.element.entityinfo;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;

public abstract class EntityInfo extends HudElement {
	public SettingSlider distance;
	
	protected EntityInfo(String name) {
		super(name);
	}
	
	public void update() {}
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {}
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
