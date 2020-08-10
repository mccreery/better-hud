package jobicade.betterhud.events;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.network.InventoryNameQuery;
import jobicade.betterhud.network.MessagePickup;
import jobicade.betterhud.network.MessageVersion;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.INameable;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.PacketTarget;

@EventBusSubscriber(modid = BetterHud.MODID)
public class ServerEvents {
    /**
     * Updates the version tracked by the proxy when a player connects to the
     * server.
     */
    @SubscribeEvent
    public static void onPlayerConnected(PlayerLoggedInEvent e) {
        if(e.getPlayer() instanceof ServerPlayerEntity) {
            ArtifactVersion version = new DefaultArtifactVersion(BetterHud.VERSION);

            PacketTarget target = PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)e.getPlayer());
            BetterHud.NET_WRAPPER.send(target, new MessageVersion(version));
        }
    }

    /**
     * Sends a message to remove any cached inventory name in the block viewer.
     */
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getWorld().getTileEntity(event.getPos()) instanceof INameable) {
            PacketTarget target = PacketDistributor.DIMENSION.with(event.getWorld().getDimension()::getType);
            BetterHud.NET_WRAPPER.send(target, new InventoryNameQuery.Response(event.getPos(), null));
        }
    }

    /**
     * Sends a message to the client for the picked up item.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onItemPickup(ItemPickupEvent e) {
        if (!e.getStack().isEmpty() && e.getPlayer() instanceof ServerPlayerEntity) {
            PacketTarget target = PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)e.getPlayer());
            BetterHud.NET_WRAPPER.send(target, new MessagePickup(e.getStack()));
        }
    }
}
