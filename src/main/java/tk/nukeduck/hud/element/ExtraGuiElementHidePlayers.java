package tk.nukeduck.hud.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.common.MinecraftForge;
import tk.nukeduck.hud.element.settings.ElementSettingBoolean;
import tk.nukeduck.hud.events.PlayerHider;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;

public class ExtraGuiElementHidePlayers extends ExtraGuiElement {
	public ElementSettingBoolean includeMe;
	
	public PlayerHider handler = new PlayerHider();
	
	@Override
	public void loadDefaults() {
		this.enabled = false;
		includeMe.value = false;
	}
	
	@Override
	public String getName() {
		return "hidePlayers";
	}
	
	@Override
	public void init() {
		//FMLCommonHandler.instance().bus().register(this.handler);
		MinecraftForge.EVENT_BUS.register(this.handler);
	}
	
	public ExtraGuiElementHidePlayers() {
		this.settings.add(includeMe = new ElementSettingBoolean("includeMe"));
	}
	
	public void update(Minecraft mc) {}
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {}

	@Override
	public boolean shouldProfile() {
		return false;
	}
}