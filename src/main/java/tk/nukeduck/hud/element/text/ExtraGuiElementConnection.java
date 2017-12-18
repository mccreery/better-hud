package tk.nukeduck.hud.element.text;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import tk.nukeduck.hud.element.settings.ElementSettingBoolean;
import tk.nukeduck.hud.element.settings.ElementSettingDivider;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.PingService;
import tk.nukeduck.hud.util.StringManager;

public class ExtraGuiElementConnection extends ExtraGuiElementText {
	private ElementSettingBoolean playerCount;
	private ElementSettingBoolean showIp;
	private ElementSettingBoolean latency;
	
	@Override
	public void loadDefaults() {
		super.loadDefaults();
		playerCount.value = true;
		showIp.value = true;
		latency.value = true;
	}
	
	@Override
	public void init() {
		FMLCommonHandler.instance().bus().register(this);
	}
	
	@Override
	public String getName() {
		return "connection";
	}
	
	private String ip = "localServer";
	long lastPing = 0;
	
	public ExtraGuiElementConnection() {
		//modes = new String[] {"left", "right"};
		//defaultMode = 1;
		super();
		this.settings.add(0, new ElementSettingDivider("position"));
		this.settings.add(new ElementSettingDivider("misc"));
		this.settings.add(playerCount = new ElementSettingBoolean("playerCount"));
		this.settings.add(showIp = new ElementSettingBoolean("showIp"));
		this.settings.add(latency = new ElementSettingBoolean("latency"));
		this.registerUpdates(UpdateSpeed.SLOW);
	}
	
	public void update(Minecraft mc) {
		if(mc.getCurrentServerData() != null) {
			//mc.mcProfiler.startSection("Ping");
			new PingService(mc.getCurrentServerData()).run();
			//mc.mcProfiler.endSection();
		}
	}
	
	@Override
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		if(mc.getCurrentServerData() != null) {
			lastPing = mc.getCurrentServerData().pingToServer;
		}
		super.render(mc, resolution, stringManager, layoutManager);
		
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
	protected String[] getText(Minecraft mc) {
		ArrayList<String> toRender = new ArrayList<String>();
		
		if(playerCount.value) {
			int players = mc.getConnection().getPlayerInfoMap().size();
			String conn = players != 1 ? FormatUtil.translatePre("strings.players", String.valueOf(players)) : FormatUtil.translatePre("strings.player");
			toRender.add(conn);
		}
		if(showIp.value) toRender.add(ip.equals("localServer") ? FormatUtil.translatePre("strings.localServer") : FormatUtil.translatePre("strings.ip", ip));
		if(latency.value) toRender.add(lastPing + "ms");
		return toRender.toArray(new String[toRender.size()]);
	}
}
