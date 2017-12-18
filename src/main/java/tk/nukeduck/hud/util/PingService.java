package tk.nukeduck.hud.util;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.ServerPinger;

public class PingService implements Runnable {
	private ServerData data;
	private static ExecutorService pool = Executors.newFixedThreadPool(5);
	
	public PingService(ServerData data) {
		this.data = data;
	}
	
	@Override
	public void run() {
		pool.execute(new PingHandler(data));
	}
}

class PingHandler implements Runnable {
	private ServerData data;
	
	private static final ServerPinger pinger = new ServerPinger();
	
	PingHandler(ServerData data) {
		this.data = data;
	}
	
	@Override
	public void run() {
		try {
			pinger.ping(data);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}