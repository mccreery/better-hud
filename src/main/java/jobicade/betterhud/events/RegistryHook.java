package jobicade.betterhud.events;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.ArmorBars;
import jobicade.betterhud.element.OverlayElement;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.RegistryBuilder;

@EventBusSubscriber(value = { Side.CLIENT }, modid = BetterHud.MODID)
public class RegistryHook {
    @SubscribeEvent
    public static void registerRegistries(RegistryEvent.NewRegistry event) {
        OverlayHook.setRegistry(new RegistryBuilder<OverlayElement>()
            .setType(OverlayElement.class)
            .setName(new ResourceLocation("element"))
            .create());
    }

    @SubscribeEvent
    public static void registerOverlayElements(RegistryEvent.Register<OverlayElement> event) {
        ArmorBars armorBars = new ArmorBars();
        // TODO don't do this here!
        armorBars.loadDefaults();
        event.getRegistry().register(armorBars);
    }
}
