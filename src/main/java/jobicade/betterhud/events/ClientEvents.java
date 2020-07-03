package jobicade.betterhud.events;

import org.lwjgl.input.Mouse;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.registry.OverlayElements;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = BetterHud.MODID, value = Side.CLIENT)
public class ClientEvents {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityDamage(LivingDamageEvent event) {
        if (!event.isCanceled() && event.getEntity().equals(Minecraft.getMinecraft().player)) {
            OverlayElements.BLOOD_SPLATTERS.onDamaged((int)event.getAmount());
        }
    }

    @SubscribeEvent
    public static void onConnect(ClientConnectedToServerEvent event) {
        if(event.isLocal()) {
            OverlayElements.CONNECTION.setLocal();
        } else {
            OverlayElements.CONNECTION.setRemote(event.getManager().getRemoteAddress());
        }
    }

    @SubscribeEvent
    public static void onClick(MouseInputEvent event) {
        if(Mouse.getEventButton() != -1 && Mouse.getEventButtonState()) {
            OverlayElements.CPS.onClick();
        }
    }

    @SubscribeEvent
    public static void onPlayerDisconnected(ClientDisconnectionFromServerEvent event) {
        OverlayElements.BLOCK_VIEWER.onChangeWorld();
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerChangedDimensionEvent event) {
        OverlayElements.BLOCK_VIEWER.onChangeWorld();
    }
}
