package tk.nukeduck.hud.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import tk.nukeduck.hud.BetterHud;

public class ServerStatusHandler {
	@SubscribeEvent
	public void onPlayerConnected(PlayerLoggedInEvent e) {
		if(!(e.player instanceof EntityPlayerMP)) return;
		BetterHud.NET_WRAPPER.sendTo(new MessageNotifyClient(), (EntityPlayerMP) e.player);
	}

	@SubscribeEvent
	public void onPlayerDisconnected(ClientDisconnectionFromServerEvent e) {
		BetterHud.proxy.notifyServer(false);
	}
}
