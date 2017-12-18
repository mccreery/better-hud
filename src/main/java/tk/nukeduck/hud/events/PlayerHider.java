package tk.nukeduck.hud.events;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tk.nukeduck.hud.BetterHud;

public class PlayerHider {
	@SubscribeEvent
	public void entityRender(RenderLivingEvent.Pre<EntityPlayer> e) {
		if(BetterHud.proxy.elements.hidePlayers.shouldRender() && e.getEntity() instanceof EntityPlayer &&
				(BetterHud.proxy.elements.hidePlayers.includeMe.value || !e.getEntity().equals(Minecraft.getMinecraft().player))) {
			e.setCanceled(true);
		}
	}
}
