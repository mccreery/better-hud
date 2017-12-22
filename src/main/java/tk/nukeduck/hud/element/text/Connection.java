package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.ArrayList;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.Divider;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.PingService;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.Ticker;

public class Connection extends TextElement {
	private SettingBoolean playerCount;
	private SettingBoolean showIp;
	private SettingBoolean latency;
	
	@Override
	public void loadDefaults() {
		super.loadDefaults();
		playerCount.value = true;
		showIp.value = true;
		latency.value = true;
	}
	
	@Override
	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	private String ip = "localServer";
	long lastPing = 0;
	
	public Connection() {
		//modes = new String[] {"left", "right"};
		//defaultMode = 1;
		super("connection");
		this.settings.add(0, new Divider("position"));
		this.settings.add(new Divider("misc"));
		this.settings.add(playerCount = new SettingBoolean("playerCount"));
		this.settings.add(showIp = new SettingBoolean("showIp"));
		this.settings.add(latency = new SettingBoolean("latency"));
		Ticker.SLOW.register(this);
	}
	
	public void update() {
		if(MC.getCurrentServerData() != null) {
			//mc.mcProfiler.startSection("Ping");
			new PingService(MC.getCurrentServerData()).run();
			//mc.mcProfiler.endSection();
		}
	}
	
	@Override
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {
		if(MC.getCurrentServerData() != null) {
			lastPing = MC.getCurrentServerData().pingToServer;
		}
		super.render(event, stringManager, layoutManager);
		
		/*if(posMode.index == 0) {
			if(playerCount.value) stringManager.add(conn, pos.value);
			if(showIp.value) stringManager.add(ip.equals("localServer") ? FormatUtil.translatePre("strings.localServer") : FormatUtil.translatePre("strings.ip", ip), pos.value);
			if(latency.value) stringManager.add(lastPing + "ms", pos.value);
		} else {
			ArrayList<String> toRender = new ArrayList<String>();
			if(playerCount.value) toRender.add(conn);
			if(showIp.value) toRender.add(ip.equals("localServer") ? FormatUtil.translatePre("strings.localServer") : FormatUtil.translatePre("strings.ip", ip));
			if(latency.value) toRender.add(lastPing + "ms");
			
			this.bounds = RenderUtil.renderStrings(mc.fontRendererObj, toRender, pos2.x, pos2.y, RenderUtil.colorRGB(255, 255, 255), Position.TOP_LEFT);
		}*/
	}
	
	@SubscribeEvent
	public void networkAction(ClientConnectedToServerEvent e) {
		if(!e.isLocal()) {
			ip = e.getManager().getRemoteAddress().toString();
		} else {
			ip = "localServer";
		}
	}

	@Override
	public boolean shouldProfile() {
		return posMode.index == 1 && (playerCount.value || showIp.value || latency.value);
	}

	@Override
	protected String[] getText() {
		ArrayList<String> toRender = new ArrayList<String>();
		
		if(playerCount.value) {
			int players = MC.getConnection().getPlayerInfoMap().size();
			String conn = I18n.format(players != 1 ? "betterHud.strings.players" : "betterHud.strings.player", players);
			toRender.add(conn);
		}
		if(showIp.value) toRender.add(I18n.format(ip.equals("localServer") ? "betterHud.strings.localServer" : "betterHud.strings.ip", ip));
		if(latency.value) toRender.add(I18n.format("betterHud.strings.ping", lastPing));
		return toRender.toArray(new String[toRender.size()]);
	}
}
