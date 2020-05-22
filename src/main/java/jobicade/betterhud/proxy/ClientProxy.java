package jobicade.betterhud.proxy;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

@EventBusSubscriber
public class ClientProxy implements HudSidedProxy {
    private ArtifactVersion serverVersion = new DefaultArtifactVersion("0.0");

    @Override
    public ArtifactVersion getServerVersion() {
        return serverVersion;
    }

    @Override
    public void setServerVersion(ArtifactVersion version) {
        serverVersion = version;
    }

	@SubscribeEvent
	public void onPlayerDisconnected(ClientDisconnectionFromServerEvent e) {
		serverVersion = new DefaultArtifactVersion("0.0");
	}
}
