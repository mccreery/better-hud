package jobicade.betterhud.events;

import static jobicade.betterhud.BetterHud.MC;

import org.lwjgl.glfw.GLFW;

import jobicade.betterhud.gui.GuiHudMenu;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

@OnlyIn(Dist.CLIENT)
public final class KeyEvents {
	private static final KeyBinding MENU_KEY = new KeyBinding("key.betterHud.open", GLFW.GLFW_KEY_U, "key.categories.misc");

	private KeyEvents() {}

	public static void registerEvents() {
		ClientRegistry.registerKeyBinding(MENU_KEY);

		MinecraftForge.EVENT_BUS.register(new KeyEvents());
	}

	@SubscribeEvent
	public void onKey(KeyInputEvent event) {
		if(MC.isGameFocused() && MENU_KEY.isPressed()) {
			MC.displayGuiScreen(new GuiHudMenu());
		}
	}
}
