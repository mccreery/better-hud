package jobicade.betterhud.element.text;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.SettingBoolean;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.I18n;

public class Connection extends TextElement {
	private SettingBoolean playerCount, showIp, latency;

	private String ip = "localServer";

	public Connection() {
		super("connection");

		settings.addChildren(
			new Legend("misc"),
			playerCount = new SettingBoolean("playerCount").setValuePrefix(SettingBoolean.VISIBLE),
			showIp = new SettingBoolean("showIp"),
			latency = new SettingBoolean("latency")
		);
	}

	public void setLocal() {
		ip = "localServer";
	}

	public void setRemote(SocketAddress address) {
		ip = address.toString();
	}

	@Override
	protected List<String> getText() {
		List<String> toRender = new ArrayList<String>(3);

		if(playerCount.get()) {
			int players = Minecraft.getMinecraft().getConnection().getPlayerInfoMap().size();
			String conn = I18n.format(players != 1 ? "betterHud.hud.players" : "betterHud.hud.player", players);
			toRender.add(conn);
		}

		if(showIp.get()) {
			toRender.add(I18n.format(ip.equals("localServer") ? "betterHud.hud.localServer" : "betterHud.hud.ip", ip));
		}

		if(latency.get() && Minecraft.getMinecraft().getCurrentServerData() != null) {
			NetworkPlayerInfo info = Minecraft.getMinecraft().getConnection().getPlayerInfo(Minecraft.getMinecraft().player.getUniqueID());

			if(info != null) {
				int ping = info.getResponseTime();
				toRender.add(I18n.format("betterHud.hud.ping", ping));
			}
		}
		return toRender;
	}
}
