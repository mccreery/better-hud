package tk.nukeduck.hud.element;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.util.RenderUtil;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

public class ExtraGuiElementConnection extends ExtraGuiElement {
	private String ip = "localServer";
	
	public ExtraGuiElementConnection() {
		name = "connection";
		modes = new String[] {"left", "right"};
		defaultMode = 1;
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		int players = mc.getNetHandler().playerInfoList.size();
		String conn = players != 1 ? I18n.format("betterHud.strings.players", new Object[0]).replace("*", String.valueOf(players)) : I18n.format("betterHud.strings.player", new Object[0]);
		//mc.mcProfiler.startSection("text");
		//RenderUtil.renderStrings(fr, width - 5, 37 + fr.FONT_HEIGHT, 0xffffff, true, conn, ip);
		if(currentMode().equals("right")) {
			rightStrings.add(conn);
			rightStrings.add(ip.equals("localServer") ? I18n.format("betterHud.strings.localServer", new Object[0]) : I18n.format("betterHud.strings.ip", new Object[0]).replace("*", ip));
		} else {
			leftStrings.add(conn);
			leftStrings.add(ip.equals("localServer") ? I18n.format("betterHud.strings.localServer", new Object[0]) : I18n.format("betterHud.strings.ip", new Object[0]).replace("*", ip));
		}
		//mc.mcProfiler.endSection();
	}
	
	@SubscribeEvent
	public void networkAction(ClientConnectedToServerEvent e) {
		if(!e.isLocal) {
			ip = e.manager.getSocketAddress().toString();
		} else {
			ip = "localServer";
		}
	}
}