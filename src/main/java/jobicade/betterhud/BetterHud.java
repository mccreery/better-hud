package jobicade.betterhud;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(BetterHud.MOD_ID)
public class BetterHud {
    public static final String MOD_ID = "better_hud";
    private static final Logger LOGGER = LogUtils.getLogger();

    public BetterHud(FMLJavaModLoadingContext context) {
    }
}
