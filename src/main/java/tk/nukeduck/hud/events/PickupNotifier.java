package tk.nukeduck.hud.events;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.network.MessagePickup;

public class PickupNotifier {
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onItemPickedUp(EntityItemPickupEvent e) {
		if(!e.isCanceled()) {
			BetterHud.netWrapper.sendTo(new MessagePickup(e.getItem().getEntityItem()), (EntityPlayerMP) e.getEntityPlayer());
		}
	}
}
