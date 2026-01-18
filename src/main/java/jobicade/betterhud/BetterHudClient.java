package jobicade.betterhud;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class BetterHudClient {
    public BetterHudClient(FMLJavaModLoadingContext context) {
        context.getModEventBus().register(this);
//        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        BetterHud.LOGGER.info("Client side code example: {}", Minecraft.getInstance().getWindow().getWindow());
    }
}
