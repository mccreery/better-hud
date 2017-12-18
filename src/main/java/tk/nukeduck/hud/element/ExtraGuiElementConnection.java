package tk.nukeduck.hud.element;

import java.util.ArrayList;

import tk.nukeduck.hud.util.FormatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

public class ExtraGuiElementConnection extends ExtraGuiElement {
	private String ip = "localServer";
	
	public ExtraGuiElementConnection() {
		name = "connection";
		modes = new String[] {"left", "right"};
		defaultMode = 1;
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		int players = mc.getNetHandler().func_175106_d().size();
		String conn = players != 1 ? FormatUtil.translatePre("strings.players", String.valueOf(players)) : FormatUtil.translatePre("strings.player");
		//mc.mcProfiler.startSection("text");
		//RenderUtil.renderStrings(fr, width - 5, 37 + fr.FONT_HEIGHT, 0xffffff, true, conn, ip);
		if(currentMode().equals("right")) {
			rightStrings.add(conn);
			rightStrings.add(ip.equals("localServer") ? FormatUtil.translatePre("strings.localServer") : FormatUtil.translatePre("strings.ip", ip));
		} else {
			leftStrings.add(conn);
			leftStrings.add(ip.equals("localServer") ? FormatUtil.translatePre("strings.localServer") : FormatUtil.translatePre("strings.ip", ip));
		}
		//mc.mcProfiler.endSection();
	}
	
	@SubscribeEvent
	public void networkAction(ClientConnectedToServerEvent e) {
		if(!e.isLocal) {
			ip = e.manager.getRemoteAddress().toString();
		} else {
			ip = "localServer";
		}
	}
}