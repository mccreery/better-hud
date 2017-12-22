package tk.nukeduck.hud.element;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;

public class GlobalSettings extends HudElement {
	public GlobalSettings() {
		super("global");
		this.settings.add(new SettingBoolean("enabled") {
			@Override
			public void set(boolean bool) {enabled = bool;}
			@Override
			public boolean get() {return enabled;}
		});
	}

	// Just a dummy element, never render
	@Override
	public boolean shouldRender() {return false;}
	@Override
	public boolean shouldProfile() {return false;}
	@Override
	public void update() {}
	@Override
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {}

	@Override
	public void loadDefaults() {
		this.enabled = true;
	}
}
