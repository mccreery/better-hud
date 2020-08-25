package jobicade.betterhud.events;

import static jobicade.betterhud.BetterHud.MC;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.gui.GuiElementList;
import jobicade.betterhud.registry.OverlayElements;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.InputEvent.MouseInputEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = BetterHud.MODID, value = Dist.CLIENT)
public class ClientEvents {
    public static void setupClient(FMLClientSetupEvent event) {
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

    @SubscribeEvent
    public static void onGuiInit(InitGuiEvent.Post event) {
        if (!(event.getGui() instanceof OptionsScreen) || MC.world == null) {
            return;
        }

        List<Widget> widgets = event.getWidgetList();
        int x = 0;
        int y = 0;

        // Find lowest button on GUI (actually just the last in the list)
        // Should be okay as long as others play by convention
        for (int i = widgets.size() - 1; i >= 0; i--) {
            Widget widget = widgets.get(i);

            if (widget.getWidth() == 150) {
                if (widget.x < event.getGui().width / 2) {
                    // Last button was left, we're right
                    x = event.getGui().width / 2 + 5;
                    y = widget.y;
                } else {
                    // Last button was right, we're left
                    x = event.getGui().width / 2 - 155;
                    y = widget.y + 24;

                    // New row so move done button down
                    widgets.stream().filter(w -> w.y > widget.y).forEach(w -> w.y += 24);
                }
                break;
            }
        }

        // Add Better HUD settings button
        event.addWidget(new Button(x, y, 150, 20, I18n.format("betterHud.menu.hudSettings") + "...", b -> {
            event.getGui().getMinecraft().displayGuiScreen(new GuiElementList(event.getGui(), BetterHud.getConfigManager()));
        }));
    }
}
