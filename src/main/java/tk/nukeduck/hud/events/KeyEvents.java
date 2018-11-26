package tk.nukeduck.hud.events;

import static tk.nukeduck.hud.BetterHud.MC;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.nukeduck.hud.gui.GuiHudMenu;

@SideOnly(Side.CLIENT)
public final class KeyEvents {
	private static final KeyBinding MENU_KEY = new KeyBinding("key.betterHud.open", Keyboard.KEY_U, "key.categories.misc");

	private KeyEvents() {}

	public static void registerEvents() {
		ClientRegistry.registerKeyBinding(MENU_KEY);

		MinecraftForge.EVENT_BUS.register(new KeyEvents());
	}

	@SubscribeEvent
	public void onKey(KeyInputEvent event) {
		if(MC.inGameHasFocus && MENU_KEY.isPressed()) {
			MC.displayGuiScreen(new GuiHudMenu());
		}
	}
}
