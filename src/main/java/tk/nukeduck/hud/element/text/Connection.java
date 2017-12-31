package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.util.Pinger;
import tk.nukeduck.hud.util.Ticker;
import tk.nukeduck.hud.util.Ticker.Tickable;

public class Connection extends TextElement implements Tickable {
	private final SettingBoolean playerCount = new SettingBoolean("playerCount");
	private final SettingBoolean showIp = new SettingBoolean("showIp");
	private final SettingBoolean latency = new SettingBoolean("latency");

	private static ExecutorService executor;

	private String ip = "localServer";

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		playerCount.set(true);
		showIp.set(true);
		latency.set(true);
	}

	@Override
	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
		Ticker.SLOW.register(this);
	}

	public Connection() {
		super("connection");

		settings.add(new Legend("misc"));
		settings.add(playerCount);
		settings.add(showIp);
		settings.add(latency);
	}

	@Override
	public void tick() {
		if(executor != null) {
			executor.submit(new Pinger(MC.getCurrentServerData()));
		}
	}

	@SubscribeEvent
	public void onConnect(ClientConnectedToServerEvent event) {
		if(!event.isLocal()) {
			executor = Executors.newSingleThreadExecutor();
			ip = event.getManager().getRemoteAddress().toString();
		} else {
			ip = "localServer";
		}
	}

	@SubscribeEvent
	public void onDisconnect(ClientDisconnectionFromServerEvent event) {
		if(executor != null) {
			executor.shutdown();
			executor = null;
		}
	}

	@Override
	protected String[] getText() {
		ArrayList<String> toRender = new ArrayList<String>();

		if(playerCount.get()) {
			int players = MC.getConnection().getPlayerInfoMap().size();
			String conn = I18n.format(players != 1 ? "betterHud.strings.players" : "betterHud.strings.player", players);
			toRender.add(conn);
		}
		if(showIp.get()) toRender.add(I18n.format(ip.equals("localServer") ? "betterHud.strings.localServer" : "betterHud.strings.ip", ip));
		if(latency.get() && MC.getCurrentServerData() != null) {
			toRender.add(I18n.format("betterHud.strings.ping", MC.getCurrentServerData().pingToServer));
		}
		return toRender.toArray(new String[toRender.size()]);
	}
}
