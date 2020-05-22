package jobicade.betterhud.proxy;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.network.MessageVersion;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

@EventBusSubscriber
public class ServerProxy implements HudSidedProxy {
    @SubscribeEvent
	public void onPlayerConnected(PlayerLoggedInEvent e) {
		if(e.player instanceof EntityPlayerMP) {
			ArtifactVersion version = new DefaultArtifactVersion(BetterHud.VERSION);
			BetterHud.NET_WRAPPER.sendTo(new MessageVersion(version), (EntityPlayerMP)e.player);
		}
	}
}
