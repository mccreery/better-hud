package jobicade.betterhud.events;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.network.InventoryNameQuery;
import jobicade.betterhud.network.MessagePickup;
import jobicade.betterhud.network.MessageVersion;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.IWorldNameable;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

@EventBusSubscriber(modid = BetterHud.MODID)
public class ServerEvents {
    /**
     * Updates the version tracked by the proxy when a player connects to the
     * server.
     */
    @SubscribeEvent
    public static void onPlayerConnected(PlayerLoggedInEvent e) {
        if(e.player instanceof EntityPlayerMP) {
            ArtifactVersion version = new DefaultArtifactVersion(BetterHud.VERSION);
            BetterHud.NET_WRAPPER.sendTo(new MessageVersion(version), (EntityPlayerMP)e.player);
        }
    }

    /**
     * Sends a message to remove any cached inventory name in the block viewer.
     */
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getWorld().getTileEntity(event.getPos()) instanceof IWorldNameable) {
            BetterHud.NET_WRAPPER.sendToDimension(
                new InventoryNameQuery.Response(event.getPos(), null),
                event.getWorld().provider.getDimension()
            );
        }
    }

    /**
     * Sends a message to the client for the picked up item.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onItemPickup(ItemPickupEvent e) {
        if (!e.getStack().isEmpty() && e.player instanceof EntityPlayerMP) {
            BetterHud.NET_WRAPPER.sendTo(
                new MessagePickup(e.getStack()),
                (EntityPlayerMP)e.player
            );
        }
    }
}
