package tk.nukeduck.hud.network.proxy;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.events.EntityInfoRenderer;
import tk.nukeduck.hud.util.SettingsIO;

public class ClientProxy extends CommonProxy {
	@Override
	public void init() {
		MinecraftForge.EVENT_BUS.register(BetterHud.entityInfoRenderer = new EntityInfoRenderer());
	}

	@Override
	public void initKeys() {
		openMenu = new KeyBinding("key.betterHud.open", Keyboard.KEY_U, "key.categories.misc");
		disable = new KeyBinding("key.betterHud.disable", Keyboard.KEY_F3, "key.categories.misc");
		ClientRegistry.registerKeyBinding(openMenu);
		ClientRegistry.registerKeyBinding(disable);
	}

	@Deprecated
	@Override
	public void initElements() {
		HudElement.initAll();
	}

	@Deprecated
	@Override
	public void loadDefaults() {
		HudElement.reloadAll();
	}

	@Override
	public void loadSettings() {
		SettingsIO.loadSettings(BetterHud.LOGGER, this);
	}

	@Override
	public void notifyServer(boolean supported) {
		HudElement.PICKUP.unsupported = !supported;
	}
}
