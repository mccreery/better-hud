package tk.nukeduck.hud.events;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.network.MessageBreeding;
import tk.nukeduck.hud.network.MessagePickup;

public class BreedInfoNotifier {
	private static final double MAX_DIST_SQ = 32.0D * 32.0D;
	
	public final HashMap<Integer, Integer> loveMap = new HashMap<Integer, Integer>();
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent e) {
		if(Minecraft.getMinecraft().theWorld == null) return;
		
		for(int entityId : loveMap.keySet()) {
			EntityAnimal entity = (EntityAnimal) Minecraft.getMinecraft().theWorld.getEntityByID(entityId);
			if(entity != null) {
				tickEntity(entity);
			} else {
				loveMap.remove(entityId);
			}
		}
	}
	
	private final void tickEntity(EntityAnimal entity) {
		int love = loveMap.get(entity.getEntityId());
		if(--love == 0) {
			loveMap.remove(entity.getEntityId());
		} else {
			loveMap.put(entity.getEntityId(), love);
		}
	}
	
	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent e) {
		if(!(e.getEntityLiving() instanceof EntityAnimal)) return;
		EntityAnimal entity = (EntityAnimal) e.getEntityLiving();
		
		if(!loveMap.containsKey(entity.getEntityId())) {
			NBTTagCompound compound = new NBTTagCompound();
			entity.writeToNBT(compound);
			
			if(compound.getInteger("InLove") > 0) {
				int love = compound.getInteger("InLove");
				loveMap.put(entity.getEntityId(), love);
				
				for(EntityPlayer player : e.getEntity().worldObj.playerEntities) {
					// TODO find a less naive way of sending this info to players
					if(player.getDistanceSqToEntity(entity) <= MAX_DIST_SQ) {
						MessageBreeding msg = new MessageBreeding(entity.getEntityId(), love);
						BetterHud.netWrapper.sendTo(msg, (EntityPlayerMP) player);
					}
				}
			}
		} else {
			tickEntity(entity);
		}
	}
}
