package tk.nukeduck.hud.util;

import java.net.UnknownHostException;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.ServerPinger;

public class Pinger implements Runnable {
	private ServerData data;
	private static final ServerPinger pinger = new ServerPinger();

	public Pinger(ServerData data) {
		this.data = data;
	}

	// TODO make ping more accurate, use existing server connection

	@Override
	public void run() {
		try {
			pinger.ping(data);
		} catch(UnknownHostException exception) {
			exception.printStackTrace();
		}
	}
}
