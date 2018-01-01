package tk.nukeduck.hud.element;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;

public class GlobalSettings extends HudElement {
	public GlobalSettings() {
		super("global");

		settings.add(new SettingBoolean(null) {
			@Override public void set(boolean bool) {settings.set(bool);}
			@Override public boolean get() {return isEnabled();}
		});
	}

	// Just a dummy element, never render
	@Override public boolean shouldRender() {return false;}
	@Override public Bounds render(RenderGameOverlayEvent event, LayoutManager manager) {return null;}

	@Override
	public void loadDefaults() {
		settings.set(true);
	}
}
