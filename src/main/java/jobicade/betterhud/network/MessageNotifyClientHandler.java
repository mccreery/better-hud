package jobicade.betterhud.network;

import java.util.function.Supplier;

import jobicade.betterhud.BetterHud;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageNotifyClientHandler {
    public static void onMessage(MessageVersion message, Supplier<NetworkEvent.Context> context) {
        BetterHud.getLogger().info("Server reported version " + message.version.getQualifier());
        BetterHud.setServerVersion(message.version);
    }
}
