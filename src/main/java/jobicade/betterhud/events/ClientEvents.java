package jobicade.betterhud.events;

import static jobicade.betterhud.BetterHud.MC;

import org.lwjgl.glfw.GLFW;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.gui.GuiElementList;
import jobicade.betterhud.registry.OverlayElements;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent.MouseInputEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = BetterHud.MODID, value = Dist.CLIENT)
public class ClientEvents {
    private static final KeyBinding menuKey = new KeyBinding("key.betterHud.open", GLFW.GLFW_KEY_U, "key.categories.misc");

    public static void setupClient(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(menuKey);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (menuKey.isPressed()) {
            MC.displayGuiScreen(new GuiElementList(BetterHud.getConfigManager()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityDamage(LivingDamageEvent event) {
        if (!event.isCanceled() && event.getEntity().equals(MC.player)) {
            OverlayElements.BLOOD_SPLATTERS.onDamaged((int)event.getAmount());
        }
    }

    @SubscribeEvent
    public static void onConnect(ClientPlayerNetworkEvent.LoggedInEvent event) {
        if (event.getNetworkManager().isLocalChannel()) {
            OverlayElements.CONNECTION.setLocal();
        } else {
            OverlayElements.CONNECTION.setRemote(event.getNetworkManager().getRemoteAddress());
        }
    }

    @SubscribeEvent
    public static void onClick(MouseInputEvent event) {
        if(event.getAction() == GLFW.GLFW_PRESS) {
            OverlayElements.CPS.onClick();
        }
    }

    @SubscribeEvent
    public static void onPlayerDisconnected(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        OverlayElements.BLOCK_VIEWER.onChangeWorld();
        BetterHud.setServerVersion(null);
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerChangedDimensionEvent event) {
        OverlayElements.BLOCK_VIEWER.onChangeWorld();
    }
}
