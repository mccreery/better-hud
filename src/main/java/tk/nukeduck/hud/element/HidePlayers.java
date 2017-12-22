package tk.nukeduck.hud.element;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.events.PlayerHider;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;

public class HidePlayers extends HudElement {
	public SettingBoolean includeMe;
	
	public PlayerHider handler = new PlayerHider();
	
	@Override
	public void loadDefaults() {
		this.enabled = false;
		includeMe.value = false;
	}
	
	@Override
	public void init() {
		//FMLCommonHandler.instance().bus().register(this.handler);
		MinecraftForge.EVENT_BUS.register(this.handler);
	}
	
	public HidePlayers() {
		super("hidePlayers");
		this.settings.add(includeMe = new SettingBoolean("includeMe"));
	}
	
	public void update() {}
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {}

	@Override
	public boolean shouldProfile() {
		return false;
	}
}