package tk.nukeduck.hud.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import tk.nukeduck.hud.element.settings.ElementSettingBoolean;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;

public class ExtraGuiElementGlobalSettings extends ExtraGuiElement {
	public ExtraGuiElementGlobalSettings() {
		this.settings.add(new ElementSettingBoolean("enabled") {
			@Override
			public void set(boolean bool) {enabled = bool;}
			@Override
			public boolean get() {return enabled;}
		});
	}

	@Override
	public String getName() {
		return "global";
	}

	// Just a dummy element, never render
	@Override
	public boolean shouldRender() {return false;}
	@Override
	public boolean shouldProfile() {return false;}
	@Override
	public void update(Minecraft mc) {}
	@Override
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {}

	@Override
	public void loadDefaults() {
		this.enabled = true;
	}
}
