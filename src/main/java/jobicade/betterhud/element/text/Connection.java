package jobicade.betterhud.element.text;

import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedInEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.ArrayList;
import java.util.List;

public class Connection extends TextElement {
    private SettingBoolean playerCount, showIp, latency;

    private String ip = "localServer";

    @Override
    public void loadDefaults() {
        super.loadDefaults();

        playerCount.set(true);
        showIp.set(true);
        latency.set(true);
    }

    @Override
    public void init(FMLClientSetupEvent event) {
        super.init(event);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public Connection() {
        super("connection");
    }

    @Override
    protected void addSettings(List<Setting<?>> settings) {
        super.addSettings(settings);
        settings.add(new Legend("misc"));
        settings.add(playerCount = new SettingBoolean("playerCount").setValuePrefix(SettingBoolean.VISIBLE));
        settings.add(showIp = new SettingBoolean("showIp"));
        settings.add(latency = new SettingBoolean("latency"));
    }

    @SubscribeEvent
    public void onConnect(LoggedInEvent event) {
        if(!event.getNetworkManager().isMemoryConnection()) {
            ip = event.getNetworkManager().getRemoteAddress().toString();
        } else {
            ip = "localServer";
        }
    }

    @Override
    protected List<String> getText() {
        List<String> toRender = new ArrayList<String>(3);

        if(playerCount.get()) {
            int players = Minecraft.getInstance().getConnection().getOnlinePlayers().size();
            String conn = I18n.get(players != 1 ? "betterHud.hud.players" : "betterHud.hud.player", players);
            toRender.add(conn);
        }

        if(showIp.get()) {
            toRender.add(I18n.get(ip.equals("localServer") ? "betterHud.hud.localServer" : "betterHud.hud.ip", ip));
        }

        if(latency.get() && Minecraft.getInstance().getCurrentServer() != null) {
            NetworkPlayerInfo info = Minecraft.getInstance().getConnection().getPlayerInfo(Minecraft.getInstance().player.getUUID());

            if(info != null) {
                int ping = info.getLatency();
                toRender.add(I18n.get("betterHud.hud.ping", ping));
            }
        }
        return toRender;
    }
}
