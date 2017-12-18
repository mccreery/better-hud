package tk.nukeduck.hud.network.proxy;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.HudElements;
import tk.nukeduck.hud.events.EntityInfoRenderer;
import tk.nukeduck.hud.util.SettingsIO;
import tk.nukeduck.hud.util.constants.Constants;

public class ClientProxy extends CommonProxy {
	@Override
	public void init() {
		MinecraftForge.EVENT_BUS.register(BetterHud.entityInfoRenderer = new EntityInfoRenderer());
	}

	public KeyBinding openMenu, disable;

	@Override
	public void initKeys() {
		this.openMenu = new KeyBinding("key.betterHud.open", Keyboard.KEY_U, "key.categories.misc");
		this.disable = new KeyBinding("key.betterHud.disable", Keyboard.KEY_F3, "key.categories.misc");
		ClientRegistry.registerKeyBinding(openMenu);
		ClientRegistry.registerKeyBinding(disable);
	}

	@Override
	public void initElements() {
		this.elements = new HudElements();
	}

	@Override
	public void loadDefaults() {
		this.elements.loadDefaults();
	}

	@Override
	public void loadSettings() {
		SettingsIO.loadSettings(Constants.LOGGER, this);
	}

	@Override
	public void notifyPresence() {
		//this.elements.breedIndicator.unsupported = false;
		this.elements.pickup.unsupported = false;
	}

	@Override
	public void clearPresence() {
		//this.elements.breedIndicator.unsupported = true;
		this.elements.pickup.unsupported = true;
	}
}
