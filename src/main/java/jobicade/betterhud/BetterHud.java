package jobicade.betterhud;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.slf4j.Logger;

@Mod(BetterHud.MOD_ID)
public class BetterHud {
    public static final String MOD_ID = "better_hud";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BetterHud(FMLJavaModLoadingContext context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> new BetterHudClient(context));

        String localVersion = context.getContainer().getModInfo().getVersion().toString();
        context.registerDisplayTest(localVersion, this::isRemoteVersionSupported);
    }

    private boolean isRemoteVersionSupported(String remoteVersionString, boolean isFromServer) {
        if (isFromServer) {
            try {
                // Example version range: 2.0+, excluding 3.0.x and 4.0+
                VersionRange remoteVersionRange = VersionRange.createFromVersionSpec("[2.0,3.0),[3.1,4.0)");
                ArtifactVersion remoteVersion = new DefaultArtifactVersion(remoteVersionString);
                return remoteVersionRange.containsVersion(remoteVersion);
            } catch (InvalidVersionSpecificationException exception) {
                throw new RuntimeException(exception);
            }
        }
        return true;
    }
}
